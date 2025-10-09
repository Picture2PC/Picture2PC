# Koin Dependency Injection Best Practices for Picture2PC

## Overview

This document outlines the Koin DI patterns and best practices used in the Picture2PC project.

## Definition Types

### `single` vs `factory`

**Use `single` when:**
- You want a singleton (one instance shared across the app)
- The object maintains state that should be shared
- The object is expensive to create

```kotlin
// ✅ Single - One TCP server for the entire app
single { SimpleTcpServer(get(), get()) }

// ✅ Single - Shared coroutine scope
single(named("tcpCoroutineScope")) { 
    CoroutineScope(get<CoroutineDispatcher>(named("defaultDispatcher")) + SupervisorJob()) 
}
```

**Use `factory` when:**
- You need a new instance each time
- The object doesn't hold shared state
- Multiple independent instances are required

```kotlin
// ✅ Factory - New socket for each network interface
factory { SimpleMulticastSocket(get(), get(), get()) }
```

## Module Organization

### Common Module Structure

```
common/
├── di/
│   ├── Modules.kt          # Root module
│   └── net/
│       ├── Modules.kt      # Network root module
│       ├── tcpModule.kt    # TCP-specific dependencies
│       └── multicastModule.kt  # Multicast-specific dependencies
```

### Module Hierarchy

```kotlin
// Root common module
val commonAppModule = module {
    includes(netAppModule)
}

// Network module includes sub-modules
val netAppModule = module {
    single<CoroutineDispatcher>(named("ioDispatcher")) { Dispatchers.IO }
    single<CoroutineDispatcher>(named("defaultDispatcher")) { Dispatchers.Default }
    
    includes(multicastModule)
    includes(tcpConnectionModule)
}
```

## Dependency Patterns

### Pattern 1: Named Qualifiers for Variants

Use named qualifiers when you have multiple instances of the same type:

```kotlin
val module = module {
    // Different dispatchers for different purposes
    single<CoroutineDispatcher>(named("ioDispatcher")) { Dispatchers.IO }
    single<CoroutineDispatcher>(named("defaultDispatcher")) { Dispatchers.Default }
    
    // Different scopes for different components
    single(named("tcpCoroutineScope")) { CoroutineScope(get(named("defaultDispatcher")) + SupervisorJob()) }
    single(named("multicastCoroutineScope")) { CoroutineScope(get(named("defaultDispatcher")) + SupervisorJob()) }
}
```

### Pattern 2: Type-Safe Definitions

Always specify types explicitly for better IDE support and type safety:

```kotlin
// ✅ Good - Type is explicit
single<CoroutineDispatcher>(named("ioDispatcher")) { Dispatchers.IO }

// ❌ Avoid - Type is implicit
single(named("ioDispatcher")) { Dispatchers.IO }
```

### Pattern 3: Constructor Injection

Prefer constructor injection over property injection:

```kotlin
// ✅ Good - Constructor injection
class TcpPayloadTransceiver(
    private val backgroundScope: CoroutineScope,
    private val tcpServer: SimpleTcpServer
) {
    // ...
}

val module = module {
    single { TcpPayloadTransceiver(get(named("tcpCoroutineScope")), get()) }
}

// ❌ Avoid - Property injection (harder to test)
class TcpPayloadTransceiver : KoinComponent {
    private val backgroundScope: CoroutineScope by inject(named("tcpCoroutineScope"))
    private val tcpServer: SimpleTcpServer by inject()
}
```

### Pattern 4: Platform-Specific Overrides

Platform modules can override common definitions:

```kotlin
// Common module
val commonAppModule = module {
    single<CoroutineDispatcher>(named("defaultDispatcher")) { Dispatchers.Default }
}

// Android module (overrides for testing or platform needs)
val androidAppModule = module {
    includes(commonAppModule)
    
    // This overrides the common definition if needed
    single<CoroutineDispatcher>(named("defaultDispatcher")) { 
        Dispatchers.Default.limitedParallelism(4) 
    }
}
```

## Testing with Koin

### Test Module Pattern

Create test-specific modules that override production dependencies:

```kotlin
val testNetModule = module {
    // Override with test dispatchers
    single<CoroutineDispatcher>(named("ioDispatcher")) { 
        UnconfinedTestDispatcher() 
    }
    single<CoroutineDispatcher>(named("defaultDispatcher")) { 
        UnconfinedTestDispatcher() 
    }
    
    // Use real implementations for other dependencies
    includes(multicastModule)
    includes(tcpConnectionModule)
}
```

### Test Setup

```kotlin
class NetworkTest {
    @Before
    fun setup() {
        startKoin {
            modules(testNetModule)
        }
    }
    
    @After
    fun tearDown() {
        stopKoin()
    }
    
    @Test
    fun testNetworking() {
        val transceiver: TcpPayloadTransceiver = get()
        // Test with injected test dispatchers
    }
}
```

### Mock Dependencies

For unit tests, inject mocks:

```kotlin
val mockModule = module {
    single<SimpleTcpServer> { mockk<SimpleTcpServer>() }
    single { TcpPayloadTransceiver(get(), get()) }
}

@Test
fun testWithMock() {
    startKoin { modules(mockModule) }
    
    val server: SimpleTcpServer = get()
    every { server.start() } returns Unit
    
    // Test code
}
```

## Common Koin DI Issues

### Issue 1: Circular Dependencies

**Problem**: A depends on B, B depends on A

**Solution**: Refactor to remove circular dependency or use lazy injection:

```kotlin
// ❌ Bad - Circular dependency
class A(val b: B)
class B(val a: A)

// ✅ Good - Break the cycle
class A(val b: B)
class B() {
    lateinit var a: A
}

val module = module {
    single { A(get()) }
    single { 
        val b = B()
        b.a = get()
        b
    }
}
```

### Issue 2: Missing Dependencies

**Problem**: Koin can't find a definition

**Solution**: Ensure module is included and definition exists:

```kotlin
// ✅ Make sure to include all required modules
startKoin {
    modules(
        commonAppModule,  // Includes network modules
        platformModule
    )
}
```

### Issue 3: Wrong Scope Type

**Problem**: Getting a new instance when you expect a singleton

**Solution**: Check if definition is `single` vs `factory`:

```kotlin
// ❌ Bad - Creates new instance each time
factory { MyExpensiveService() }

// ✅ Good - Shares one instance
single { MyExpensiveService() }
```

## Module Configuration

### Current Module Structure

```kotlin
// Common Network Module
val netAppModule = module {
    // Dispatchers
    single<CoroutineDispatcher>(named("ioDispatcher")) { Dispatchers.IO }
    single<CoroutineDispatcher>(named("defaultDispatcher")) { Dispatchers.Default }
    
    includes(multicastModule)
    includes(tcpConnectionModule)
}

// TCP Module
val tcpConnectionModule = module {
    single(named("tcpCoroutineScope")) { 
        CoroutineScope(get<CoroutineDispatcher>(named("defaultDispatcher")) + SupervisorJob()) 
    }
    single { SimpleTcpServer(get(named("tcpCoroutineScope")), get(named("ioDispatcher"))) }
    single { TcpPayloadTransceiver(get(named("tcpCoroutineScope")), get()) }
}

// Multicast Module
val multicastModule = module {
    single(named("multicastCoroutineScope")) { 
        CoroutineScope(get<CoroutineDispatcher>(named("defaultDispatcher")) + SupervisorJob()) 
    }
    single { MulticastPayloadTransceiver(get(named("multicastCoroutineScope"))) }
    factory { SimpleMulticastSocket(get(named("multicastCoroutineScope")), get(named("ioDispatcher")), get()) }
    single { InetSocketAddress(MulticastConstants.ADDRESS, MulticastConstants.PORT) }
}
```

### Desktop Module

```kotlin
val appModule = module {
    includes(commonAppModule)
    
    single<CoroutineDispatcher>(named("defaultDispatcher")) { Dispatchers.Default }
    single(named("backgroundCoroutineScope")) { 
        CoroutineScope(get<CoroutineDispatcher>(named("defaultDispatcher")) + SupervisorJob()) 
    }
    single(named("viewModelCoroutineScope")) { 
        CoroutineScope(get<CoroutineDispatcher>(named("defaultDispatcher")) + SupervisorJob()) 
    }
    
    single<DataTransmitter> { MulticastTcpDataTransmitter(get(), get(), get(), get(named("backgroundCoroutineScope"))) }
    single<ServerPreferencesRepository> { TestServerPreferencesRepository() }
    single<PicturePreparation> { PicturePreparationImpl() }
    
    single { ServersSectionViewModel(get()) }
    single { MovementHandlerViewModel() }
    single { PictureDisplayViewModel(get(named("viewModelCoroutineScope")), get(), get(), get()) }
}
```

### Android Module

```kotlin
val appModule = module {
    includes(commonAppModule)
    
    single(named("defaultDispatcher")) { Dispatchers.Default }
    single(named("backgroundCoroutineScope")) { 
        CoroutineScope(get<CoroutineDispatcher>(named("defaultDispatcher")) + SupervisorJob()) 
    }
    
    single<DataTransmitter> { MulticastTcpDataTransmitter(get(), get(), get(), get(named("backgroundCoroutineScope"))) }
    single<ServerPreferencesRepository> { DataStoreServerPreferencesRepository(get(), get(named("backgroundCoroutineScope"))) }
    
    single { BroadcastViewModel(get()) }
    single<EdgeDetect> { YOLOv8SegEdgeDetect(get(named("ioDispatcher"))) }
    single { ClientsViewModel(get()) }
    single<PictureManager> { CameraPictureManager(get(), get(), get(named("backgroundCoroutineScope")), get(named("defaultDispatcher"))) }
    single { CameraViewModel(get(), get()) }
    single { ScreenSelectorViewModel() }
    
    single { SavedStateHandle() }
}
```

## Best Practices Summary

1. **Use type-safe definitions**: Always specify types explicitly
2. **Prefer constructor injection**: Easier to test and understand
3. **Organize by feature**: Group related dependencies in modules
4. **Use named qualifiers**: For multiple instances of same type
5. **Make dependencies testable**: Allow test modules to override production dependencies
6. **Document module dependencies**: Use `includes()` to show module relationships
7. **Choose correct scope**: `single` for shared state, `factory` for independent instances

## References

- [Koin Documentation](https://insert-koin.io/)
- [Koin Testing](https://insert-koin.io/docs/reference/koin-test/testing)
- [Kotlin DI Best Practices](https://kotlinlang.org/docs/dependency-injection.html)
