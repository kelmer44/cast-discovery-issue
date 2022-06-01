# CAST DISCOVERY ISSUES


In order to reproduce the issue, first set up your receiver Id on `strings.xml`. This receiver app should be prepared to just intake and play plain URLs.

This small app reproduces both the lack of discovery and lack of reconnection when the CastContext initialization is delayed.

The Cast SDK only starts the ReconnectionService after content has been played, so in order to repro, do the following:

1. Start the app, the SDK will be initialized after 2 seconds.
2. Once the buttons are enabled, hit trigger discovery. This will make the MediaRouteButton become enabled.
3. Connect to a Chromecast device supported by the receiver Id (you might have to allow list your device)
4. Click on Play content to initiate playback of a sample video
5. Force close the app
6. Start the app again. Discovery wont happen.
7. Click on Trigger Discovery to prompt discovery.
8. Notice Reconnection does not happen, when it should.

