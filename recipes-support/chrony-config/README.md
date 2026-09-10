# RDK Chrony Configuration

This recipe provides a script and configuration for updating the RDK chrony NTP server configuration with a partner URL.

## Files

- **update-chrony-config.sh**: Script that reads partner NTP server URL and updates `/etc/rdk_chrony.conf`
- **partner_url.conf**: Configuration file containing the partner NTP server URL

## Usage

### Method 1: Using Configuration File

1. Edit `/etc/partner_url.conf` and set the partner NTP URL:
   ```
   PARTNER_NTP_URL=ntp.partner.example.com
   ```

2. Run the update script:
   ```bash
   /usr/sbin/update-chrony-config.sh
   ```

3. The script will create/update `/etc/rdk_chrony.conf` with the format:
   ```
   server ntp.partner.example.com iburst minpoll 10 maxpoll 12
   ```

### Method 2: Using Environment Variable

You can also set the partner URL via environment variable:

```bash
PARTNER_NTP_URL=ntp.partner.example.com /usr/sbin/update-chrony-config.sh
```

## Configuration Format

The generated `/etc/rdk_chrony.conf` file contains a single line in the chrony server format:
```
server <PARTNER_URL> iburst minpoll 10 maxpoll 12
```

Where:
- `server`: Chrony directive for NTP server
- `<PARTNER_URL>`: The partner NTP server URL/hostname
- `iburst`: Send burst of packets on startup for faster sync
- `minpoll 10`: Minimum polling interval (2^10 = 1024 seconds)
- `maxpoll 12`: Maximum polling interval (2^12 = 4096 seconds)

## Integration

To include this in your RDK build, add `chrony-config` to your image recipe:

```bitbake
IMAGE_INSTALL += "chrony-config"
```
