# Group Functionality Implementation

## Overview
This implementation adds group-based device management to Picture2PC, allowing users to organize devices into groups and control which devices can receive pictures.

## Key Features

### 1. Persistent Device UUID
- Each device now has a persistent UUID stored in preferences
- UUID is generated on first launch and saved to storage
- UUID is reused across app restarts to maintain device identity

### 2. Group Management
- Groups are identified by a UUID and have a user-friendly name
- Group information is stored in preferences (groupUuid, groupName)
- Users can create a new group by setting a group name
- Group name can only be set on first group creation (as per requirements)

### 3. Group Verification
- When devices connect via TCP, they exchange group verification hashes
- Hash is computed as: `hash(groupUuid + ":" + deviceUuid)`
- This ensures both devices are in the same group before allowing picture transmission
- Verification status is tracked per device and displayed in UI

### 4. Per-Device Receive Control
- Each connected device has a toggle switch to enable/disable receiving pictures
- Toggle is only available for devices that have been group-verified
- This allows fine-grained control over which group members receive pictures

## Implementation Details

### Data Model Changes

#### PreferencesRepository
Added three new fields:
- `deviceUuid: StateFlow<String>` - Persistent device identifier
- `groupUuid: StateFlow<String>` - Group identifier
- `groupName: StateFlow<String>` - Human-readable group name

#### DefaultDevice
Extended to include:
- `uuid: String` - Device identifier
- `canReceive: StateFlow<Boolean>` - Whether this device can receive pictures
- `groupVerified: StateFlow<Boolean>` - Whether group verification succeeded

#### TcpPayload
Added two new payload types:
- `GroupVerification(groupHash: String, targetPeer: Peer)` - Sends group hash for verification
- `GroupVerified(verified: Boolean, targetPeer: Peer)` - Response with verification result

### Network Flow

1. **Device Discovery** (unchanged)
   - Multicast packets are sent to discover devices on the network
   - TCP connections are established with discovered devices

2. **Group Verification** (new)
   - After TCP connection is established, each device sends a `GroupVerification` payload
   - The receiving device computes the expected hash and sends back `GroupVerified`
   - Only if verification succeeds is the device added to the "can receive" list

3. **Picture Transmission** (modified)
   - Before sending a picture, the system checks:
     - Is the target device group-verified?
     - Is the target device's canReceive flag set to true?
   - Pictures are only sent if both conditions are met
   - Received pictures are only accepted from group-verified devices

### UI Components

#### Android
- `GroupInputField.kt` - Input field for group name with "Create Group" button
- `ConnectedClientsList.kt` - Updated to show:
  - Group verification status ("✓ In Group" / "✗ Not in Group")
  - Toggle switch for receive permission (only for verified devices)

#### Desktop
- `GroupInputField.kt` - Similar to Android version
- `ConnectionInfo.kt` - Updated with same features as Android

### Configuration

Users need to:
1. Set a group name on first launch
2. Click "Create Group" button (only shown if no group exists)
3. All devices in the same group should use the same group UUID (implementation note: the current design uses per-device UUIDs; for full group functionality, devices should share a common group UUID - this could be implemented via QR code, manual entry, or group invitation system)

## Future Enhancements

The current implementation provides the foundation. Potential improvements:
1. Group joining mechanism (QR code, invitation link, or manual UUID entry)
2. Multiple group support
3. Group member list/management UI
4. Group leave/delete functionality
5. Encrypted group communication

## Testing Notes

To test the implementation:
1. Run the app on two devices
2. Set the same group name on both devices and create the group
3. Verify devices appear in the connection list
4. Check that group verification status is displayed
5. Toggle the receive switch and verify picture sending works only when enabled
6. Test with devices in different groups to verify they can't exchange pictures
