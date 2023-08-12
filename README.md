# Remote Music

Remote Music is an app for Android, to control media of your computer from your phone.


# Features
- changing volume with volume buttons of your phone, even with screen off
- next/previous track and play/pause buttons in notification
- if you have smartwatch/band with music control functionality, you can use it to manage media of a computer


# Limitations
- the app works only in local network, so you have to be connected to the same WiFi as your computer
- you have to run, at all time, a server instance of the app, on you computer, for anything to work


# Getting started
1. Download latest .apk from [releases](https://github.com/Ynfuien/RemoteMusic/releases) page, and install it on your phone.
2. Go to [RemoteMusicServer](https://github.com/Ynfuien/RemoteMusicServer) page, and install it as well, but on your pc.
3. Connect both devices to the same network
4. Check local ip of your computer. You can do that by using `ipconfig` command in Windows console and finding `IPv4 Address` line. Then enter this ip in android app, and hit save button.
5. If you want, you can use different port for communication between devices. In that case, just change it in both places - in android app and windows server.
6. Click start button in the app and use it at your will!


# Screenshots
<p align="center">
  <img src="https://i.imgur.com/PtzCmZk.jpg" alt="App" width="300px">
    &nbsp;
  <img src="https://i.imgur.com/t8PGAt7.jpg" alt="Notification" width="300px">
</p>

# Some last words...
This project was made mainly for my personal use, because I hated that I needed to stand up from my couch, just to change music volume on my pc. Also e.g [UnifiedRemote](https://www.unifiedremote.com/) didn't cut it, cause it only allows you to manage media from the app itself, and not by using volume buttons. Aand whether there was another app doing exacly what I wanted, I didn't bother searching for it xD

So, the app is as it is.
- there is no password or user system
- it only works on local network (it could work outside lan, if you forwarded some ports, but it would be insecure)
- everyone with this app, in the same network as you, can manage media on you computer. The only "barrier" is your port, that you can change to almost random
- app (server and mobile), is not beautiful, and will never be


# License
This project uses [GNU GPLv3](https://github.com/Ynfuien/RemoteMusicServer/blob/main/LICENSE) license.