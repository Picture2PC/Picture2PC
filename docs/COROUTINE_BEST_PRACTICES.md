# Coroutine Best Practices for Picture2PC

## Overview

This document outlines the coroutine patterns and best practices used in the Picture2PC project.

## Core Principles

### 1. Use SupervisorJob for Long-Lived Scopes

**Why**: SupervisorJob ensures that a failure in one child coroutine doesn't cancel sibling coroutines.

```kotlin
// ✅ Good - Failures are isolated
val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

// ❌ Bad - One failure cancels all children
val scope = CoroutineScope(Dispatchers.Default)
```

**Where we use it**:
- `tcpCoroutineScope` - TCP server and client scopes
- `multicastCoroutineScope` - Multicast socket scopes
- `backgroundCoroutineScope` - Background work scopes
- `viewModelCoroutineScope` - ViewModel scopes

### 2. Inject Dispatchers via Koin DI

**Why**: Makes code testable by allowing test dispatchers to be injected.

```kotlin
// ✅ Good - Testable
val netAppModule = module {
    single<CoroutineDispatcher>(named("ioDispatcher")) { Dispatchers.IO }
    single<CoroutineDispatcher>(named("defaultDispatcher")) { Dispatchers.Default }
}

// ❌ Bad - Hard to test
val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
```

**Available dispatchers**:
- `ioDispatcher` - For I/O operations (file, network)
- `defaultDispatcher` - For CPU-intensive work

### 3. Create Child Scopes with SupervisorJob

**Why**: Allows parent scope to continue even if a child fails.

```kotlin
// ✅ Good - Client failures don't affect server
val client = SimpleTcpClient(
    backgroundScope + SupervisorJob(),
    ioDispatcher,
    socket
)

// ❌ Bad - Client failure cancels parent
val client = SimpleTcpClient(
    backgroundScope + Job(),
    ioDispatcher,
    socket
)
```

## Dependency Injection Patterns

### Common Module

The common module provides base dispatcher definitions:

```kotlin
val netAppModule = module {
    single<CoroutineDispatcher>(named("ioDispatcher")) { Dispatchers.IO }
    single<CoroutineDispatcher>(named("defaultDispatcher")) { Dispatchers.Default }
    
    includes(multicastModule)
    includes(tcpConnectionModule)
}
```

### Platform Modules (Android/Desktop)

Platform modules can override dispatchers if needed:

```kotlin
val appModule = module {
    includes(commonAppModule)
    
    // Optional: Override for platform-specific needs
    single<CoroutineDispatcher>(named("defaultDispatcher")) { 
        Dispatchers.Default.limitedParallelism(4) 
    }
    
    // Create application-specific scopes
    single(named("backgroundCoroutineScope")) { 
        CoroutineScope(get<CoroutineDispatcher>(named("defaultDispatcher")) + SupervisorJob()) 
    }
}
```

## Testing

### Test Dispatcher Injection

For unit tests, override dispatchers with test dispatchers:

```kotlin
val testModule = module {
    single<CoroutineDispatcher>(named("ioDispatcher")) { 
        UnconfinedTestDispatcher() 
    }
    single<CoroutineDispatcher>(named("defaultDispatcher")) { 
        UnconfinedTestDispatcher() 
    }
}

@Test
fun testWithTestDispatchers() {
    startKoin {
        modules(testModule, /* other modules */)
    }
    // Test code here
}
```

### Testing Scope Cancellation

Verify that SupervisorJob properly isolates failures:

```kotlin
@Test
fun `child failure does not cancel siblings`() = runTest {
    val scope = CoroutineScope(UnconfinedTestDispatcher() + SupervisorJob())
    
    var sibling1Completed = false
    var sibling2Completed = false
    
    scope.launch {
        delay(100)
        sibling1Completed = true
    }
    
    scope.launch {
        throw RuntimeException("Test failure")
    }
    
    scope.launch {
        delay(100)
        sibling2Completed = true
    }
    
    advanceUntilIdle()
    
    assertTrue(sibling1Completed)
    assertTrue(sibling2Completed)
}
```

## Common Patterns

### Pattern 1: Network Socket Lifecycle

```kotlin
class SocketHandler(
    private val scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher
) {
    fun startListening() {
        scope.launch {
            while (isActive) {
                // Receiving work on IO dispatcher
                val data = withContext(ioDispatcher) {
                    socket.receive()
                }
                // Process on current dispatcher
                processData(data)
            }
        }
    }
}
```

### Pattern 2: Parent-Child Scope Relationships

```kotlin
class TcpServer(
    private val backgroundScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun acceptClient() {
        val socket = withContext(ioDispatcher) {
            serverSocket.accept()
        }
        
        // Create isolated scope for client
        val client = SimpleTcpClient(
            backgroundScope + SupervisorJob(),
            ioDispatcher,
            socket
        )
    }
}
```

### Pattern 3: Flow Collection with Lifecycle

```kotlin
class DataTransmitter(
    private val backgroundScope: CoroutineScope
) {
    init {
        // Collect flows tied to scope lifecycle
        tcpTransceiver.receivedPayloads
            .onEach { payload -> 
                handlePayload(payload) 
            }
            .launchIn(backgroundScope)
    }
}
```

## Migration Guide

If you're updating existing code:

1. **Replace hardcoded dispatchers**:
   ```kotlin
   // Before
   CoroutineScope(Dispatchers.Default + SupervisorJob())
   
   // After
   CoroutineScope(get<CoroutineDispatcher>(named("defaultDispatcher")) + SupervisorJob())
   ```

2. **Add SupervisorJob to scopes**:
   ```kotlin
   // Before
   CoroutineScope(Dispatchers.Default)
   
   // After
   CoroutineScope(get<CoroutineDispatcher>(named("defaultDispatcher")) + SupervisorJob())
   ```

3. **Fix child scope creation**:
   ```kotlin
   // Before
   parentScope + Job()
   
   // After
   parentScope + SupervisorJob()
   ```

## Troubleshooting

### Issue: All coroutines cancel when one fails

**Cause**: Missing SupervisorJob in scope creation.

**Solution**: Add SupervisorJob to the scope:
```kotlin
CoroutineScope(dispatcher + SupervisorJob())
```

### Issue: Tests hang or don't complete

**Cause**: Production dispatchers in tests.

**Solution**: Inject test dispatchers:
```kotlin
single<CoroutineDispatcher>(named("ioDispatcher")) { 
    UnconfinedTestDispatcher() 
}
```

### Issue: Scope leaks (coroutines continue after component is destroyed)

**Cause**: Scope not properly cancelled.

**Solution**: Cancel scope in cleanup:
```kotlin
override fun onCleared() {
    scope.cancel()
}
```

## References

- [Kotlin Coroutines Best Practices](https://kotlinlang.org/docs/coroutines-best-practices.html)
- [Coroutine Context and Dispatchers](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html)
- [Koin Documentation](https://insert-koin.io/)
