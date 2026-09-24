# 💬 Messenger Desktop Application

A modern, high-performance desktop chat application built with **Java Swing** and a hybrid **TCP / UDP Socket Networking** architecture. The application delivers a Facebook Messenger Web-inspired UI, complete with real-time text messaging, binary file transfers, live typing indicators, online user discovery, and dynamic **Dark/Light/Auto Theme** switching.

---

## 🧭 Your First 10 Minutes

If you just cloned this repository, follow this quick guided tour to see the project in action:

1. **Compile the project**:
   ```powershell
   mkdir bin
   javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName)
   ```
2. **Launch the Server Console**:
   ```powershell
   java -cp bin server.TCPServer
   ```
   *The Server Admin Console will open, starting the TCP listener on Port 2020 and UDP listener on Port 2021.*

3. **Launch the First Client ("User A")**:
   ```powershell
   java -cp bin client.TCPClient
   ```
   *Enter username `Alice`, leave Server IP as `127.0.0.1`, pick `Tối (Dark Mode)`, and click **Bắt đầu Chat**.*

4. **Launch the Second Client ("User B")**:
   *Open another terminal window and run:*
   ```powershell
   java -cp bin client.TCPClient
   ```
   *Enter username `Bob`, leave Server IP as `127.0.0.1`, pick `Sáng (Light Mode)`, and click **Bắt đầu Chat**.*

5. **Send a Message & Test Live Features**:
   - Click `Alice` in Bob's sidebar.
   - Type a message and watch `Bob đang soạn tin...` animate live on Alice's screen.
   - Press **Enter** to send. Send a file using the 📄 file attachment button!
   - Toggle theme modes anytime via the moon/sun icon in the top header.

---

## 📌 Table of Contents

- [🧠 Project Overview](#-project-overview)
- [🛠 Technology Stack](#-technology-stack)
- [🏗 System Architecture](#-system-architecture)
- [📁 Repository Structure](#-repository-structure)
- [⚙️ Prerequisites & Installation](#️-prerequisites--installation)
- [🚀 Running the Project](#-running-the-project)
- [🗺 "Where Should I Start?"](#-where-should-i-start)
- [🔄 Protocol Specification & Data Flow](#-protocol-specification--data-flow)
- [🎨 Custom Design System & Themes](#-custom-design-system--themes)
- [🔧 Common Development Workflows](#-common-development-workflows)
- [🧪 Testing & Build](#-testing--build)
- [🚨 Troubleshooting](#-troubleshooting)
- [📖 Glossary](#-glossary)

---

## 🧠 Project Overview

### What is this project?
The **Messenger Desktop Application** is a peer-to-peer style client-server chat client built natively in Java. It allows users connected across a Local Area Network (LAN) or localhost to discover each other automatically, chat in real time, transfer files, and monitor network statistics.

### Key Features
- ⚡ **Hybrid TCP & UDP Sockets**: TCP ensures 100% reliable delivery for chat messages and binary files; UDP enables lightweight online presence discovery and low-latency typing indicators.
- 🎨 **Messenger Web Aesthetic**: Custom anti-slop Java Swing UI featuring vector icons (`Graphics2D`), smooth rounded input fields (`RoundRectangle2D`), avatar initial badges, and dynamic chat bubbles.
- 🌓 **Dynamic Theme Engine**: Supports **Dark Mode** (`#18191A`), **Light Mode** (`#F0F2F5`), **Auto-Time Mode** (6:00-18:00 Light, 18:00-6:00 Dark), and **System Sync**.
- 📂 **Binary File Transfer**: Send images, PDFs, documents, or archives to any online user with download-to-disk prompts.
- ✍️ **Vector Animated Typing Indicators**: Live 3-dot pulsing indicator informing recipients when their chat partner is typing.
- 🖥 **Server Admin Console**: Real-time dashboard displaying connected TCP clients, active ports, client access logs, and live connection counters.

---

## 🛠 Technology Stack

| Layer | Technology | Purpose |
| --- | --- | --- |
| **Language** | Java 17+ (Core Java & Swing) | Core programming runtime |
| **Client Frontend** | Java Swing (`JFrame`, `JPanel`, `Graphics2D`, `BasicComboBoxUI`) | Custom anti-slop UI rendering without third-party LookAndFeel dependencies |
| **Server Admin UI** | Java Swing | Dashboard interface for server monitoring and client tracking |
| **Reliable Network** | Java TCP Sockets (`ServerSocket`, `Socket`, `DataInputStream`) | Guaranteed transmission for text messages and binary file payloads (Port 2020) |
| **Realtime Network** | Java UDP Sockets (`DatagramSocket`, `DatagramPacket`) | Heartbeat PING/PONG presence detection and live typing status broadcasts (Port 2021) |
| **Concurrency** | Java Threads & `ConcurrentHashMap` | Thread-safe socket dispatching and background timer tasks |
| **Build Tools** | Standard JDK Compiler (`javac`, `java`) | Native Java execution without complex external build tool requirements |

---

## 🏗 System Architecture

The application uses a **Hybrid Dual-Socket Architecture** separating reliable data delivery from lightweight status broadcasting:

```text
  +-------------------------------------------------------------+
  |                     Client Application                      |
  |  [LoginDialog] -> [ChatUI] -> [OnlinePanel] + [ChatPanel]   |
  +------------------------------+------------------------------+
                                 |
                 +---------------+---------------+
                 |                               |
                 v (Port 2020)                   v (Port 2021)
          TCP Socket Connection            UDP Datagram Packets
                 |                               |
                 |  - Account Login Registration |  - Online Heartbeats (PING/LOGIN/LOGOUT)
                 |  - 1-on-1 Text Messaging      |  - Online User Broadcast List
                 |  - Binary File Transfers      |  - Live Typing Indicator Packets
                 |                               |
                 v                               v
  +-------------------------------------------------------------+
  |                     Server Application                      |
  |  [ServerUI] Dashboard <-> [TCPServer] + [UDPOnlineServer]   |
  +-------------------------------------------------------------+
```

### Request & Data Flow
1. **User Login**: `LoginDialog` prompts user for display name, IP, and theme choice.
2. **TCP Registration**: `TCPClient` connects to Server Port `2020`. `ClientHandler` validates name uniqueness and registers the socket into `ConcurrentHashMap`.
3. **UDP Discovery**: `UDPOnlineClient` sends a `LOGIN|<username>` UDP packet to Server Port `2021`. The `UDPOnlineServer` updates its online table and broadcasts an updated user list (`ONLINE\nuser1\nuser2...`) to all clients.
4. **Heartbeat Loop**: Client sends `PING|<username>` every 3 seconds over UDP. If no ping is received for 10 seconds, `UDPOnlineServer` marks the user timed out and broadcasts the new online list.
5. **Text Messaging**: `ChatPanel` sends `MESSAGE|<target>|<text>` over TCP. Server routes it to the target user's `ClientHandler`.
6. **File Transfer**: Client reads file bytes and sends `FILE|<target>|<filename>|<byte_length>` followed by raw byte data over TCP. Target receives a download prompt.
7. **Typing Status**: As the user types in `txtMessage`, `TypingClientService` sends `TYPING|<sender>|<target>|1` over UDP. Target UI displays `TypingIndicator`.

---

## 📁 Repository Structure

```text
GiuaKi/
├── src/
│   ├── client/
│   │   ├── ClientConnection.java     # Manages TCP socket connection, reader thread, and outgoing messages/files
│   │   ├── TCPClient.java            # Main client controller, orchestrating UI and networking handlers
│   │   ├── TypingClientService.java  # Sends UDP typing status updates to the server
│   │   └── UDPOnlineClient.java      # Manages UDP heartbeat pings and receiving online user updates
│   │
│   ├── server/
│   │   ├── ClientHandler.java        # Dedicated per-client TCP thread handling protocol messages
│   │   ├── ServerUI.java             # Admin dashboard UI frame displaying ports, active count, and logs
│   │   ├── TCPServer.java            # TCP ServerSocket listener (Port 2020) and main server entry point
│   │   └── UDPOnlineServer.java      # UDP DatagramSocket listener (Port 2021) and presence manager
│   │
│   └── ui/
│       ├── ChatPanel.java            # Main chat area: messages list, dynamic speech bubbles, file download cards
│       ├── ChatUI.java               # Top-level window frame assembling OnlinePanel and ChatPanel
│       ├── LoginDialog.java          # Modern login & theme selection dialog
│       ├── OnlinePanel.java           # Left sidebar containing online users list, search input, and filter tabs
│       ├── ThemeManager.java         # Central theme token registry (Dark, Light, Auto-Time, System modes)
│       ├── TypingIndicator.java      # Custom animated 3-dot vector indicator for typing status
│       └── UIIcons.java              # Vector graphic icons rendered using Java Graphics2D
│
├── .gitignore                        # Git exclusion rules
└── README.md                         # Primary onboarding & developer guide
```

### Important Directories & Files

| Path / File | Purpose | Beginner Takeaway |
| --- | --- | --- |
| `src/ui/` | All Swing user interface components | **Start here for UI / UX modifications** |
| `src/client/` | Client network logic & controllers | **Start here for client socket / protocol logic** |
| `src/server/` | Server network listeners & UI dashboard | **Start here for server socket / routing logic** |
| `src/ui/ThemeManager.java` | Central palette & theme tokens | **Modify colors or add new theme modes here** |
| `src/ui/UIIcons.java` | Custom vector graphics drawing routines | **Add or edit icons here without external images** |

---

## ⚙️ Prerequisites & Installation

### Prerequisites
Before getting started, ensure you have installed:

1. **Java Development Kit (JDK)**: Version **17** or higher.
   - Verify: `java -version` and `javac -version`
2. **Git**: Version **2.x** or higher.
   - Verify: `git --version`

### Installation Step-by-Step

1. **Clone the repository**:
   ```bash
   git clone https://github.com/hotanhuy2610/GiuaKiLapTrinhMang.git
   cd GiuaKiLapTrinhMang/GiuaKi
   ```
2. **Create the output binary directory**:
   ```powershell
   mkdir bin
   ```
3. **Compile the source code**:
   ```powershell
   javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName)
   ```

---

## 🚀 Running the Project

### Ports Overview
| Service | Protocol | Default Port | Purpose |
| --- | --- | --- | --- |
| TCP Chat Server | TCP | `2020` | Reliable text messages & file data transfer |
| UDP Online Server | UDP | `2021` | Heartbeat PINGs, online lists, typing indicators |

### Option A: Running from Terminal / PowerShell

1. **Start the Server Application**:
   ```powershell
   java -cp bin server.TCPServer
   ```
   *This starts the `ServerUI` dashboard along with TCP (Port 2020) and UDP (Port 2021) listeners.*

2. **Start Client 1**:
   ```powershell
   java -cp bin client.TCPClient
   ```

3. **Start Client 2** (in another terminal window):
   ```powershell
   java -cp bin client.TCPClient
   ```

### Option B: Running from an IDE (IntelliJ IDEA / Eclipse / VS Code)

1. Open the project folder `GiuaKi` in your IDE.
2. Ensure project SDK is set to JDK 17 or higher.
3. Run the Server: Open `src/server/TCPServer.java` and click **Run**.
4. Run the Client: Open `src/client/TCPClient.java` and click **Run**.
5. *Note for IntelliJ users*: To run multiple Clients simultaneously, open **Run/Debug Configurations** for `TCPClient` and check **"Allow multiple instances"** / **"Allow parallel run"**.

---

## 🗺 "Where Should I Start?"

| I want to... | Start in this file | Primary method/class |
| --- | --- | --- |
| **Change app colors / palette** | [ThemeManager.java](file:///d:/huy/GiuaKiLapTrinhMang/GiuaKi/src/ui/ThemeManager.java) | `Dark` & `Light` static color classes |
| **Add a new vector icon** | [UIIcons.java](file:///d:/huy/GiuaKiLapTrinhMang/GiuaKi/src/ui/UIIcons.java) | Add static `Icon create...Icon(...)` method |
| **Modify chat bubbles / messages** | [ChatPanel.java](file:///d:/huy/GiuaKiLapTrinhMang/GiuaKi/src/ui/ChatPanel.java) | `addMessage()` & `showFile()` |
| **Modify sidebar user list** | [OnlinePanel.java](file:///d:/huy/GiuaKiLapTrinhMang/GiuaKi/src/ui/OnlinePanel.java) | `onlineList` cell renderer |
| **Modify login window** | [LoginDialog.java](file:///d:/huy/GiuaKiLapTrinhMang/GiuaKi/src/ui/LoginDialog.java) | `showDialog()` |
| **Modify network protocol** | [ClientHandler.java](file:///d:/huy/GiuaKiLapTrinhMang/GiuaKi/src/server/ClientHandler.java) | `run()` packet type `if/else` block |
| **Modify server dashboard** | [ServerUI.java](file:///d:/huy/GiuaKiLapTrinhMang/GiuaKi/src/server/ServerUI.java) | `ServerUI` constructor |

---

## 🔄 Protocol Specification & Data Flow

### TCP Messages (Port 2020)
All TCP messages use UTF-8 strings encoded via `DataInputStream.readUTF()` / `DataOutputStream.writeUTF()`.

1. **Registration**: First string sent upon connection is the `username`.
2. **Text Message**:
   - Outgoing: `MESSAGE` -> `targetUsername` -> `messageText`
   - Incoming: `MESSAGE` -> `senderUsername` -> `messageText`
3. **File Transfer**:
   - Outgoing: `FILE` -> `targetUsername` -> `fileName` -> `fileSize` (int) -> `rawBytes`
   - Incoming: `FILE` -> `senderUsername` -> `fileName` -> `fileSize` (int) -> `rawBytes`
4. **Error Notification**: `ERROR` -> `errorMessageText`

### UDP Messages (Port 2021)
All UDP packets use UTF-8 formatted string payloads.

1. **Login Broadcast**: `LOGIN|<username>`
2. **Heartbeat Ping**: `PING|<username>`
3. **Logout Broadcast**: `LOGOUT|<username>`
4. **Online List Update**: `ONLINE\n<user1>\n<user2>...`
5. **Typing Indicator**: `TYPING|<sender>|<target>|<1_or_0>`

---

## 🎨 Custom Design System & Themes

The project uses custom `Graphics2D` rendering with zero third-party GUI library dependencies:

- **Antialiasing**: Enforced on all drawing contexts via `RenderingHints.KEY_ANTIALIASING`.
- **Theme Modes**:
  - `DARK`: Deep Messenger Slate (`#18191A` app bg, `#242526` card bg, `#0084FF` active accent).
  - `LIGHT`: Clean Messenger White (`#F0F2F5` app bg, `#FFFFFF` card bg, `#0084FF` active accent).
  - `AUTO_TIME`: Dynamically calculates `LocalTime.now().getHour()` (Light between 06:00-18:00, Dark between 18:00-06:00).
  - `SYSTEM`: Defaults to dark mode for developer environments.
- **Theme Listener Pattern**: Components register `ThemeManager.addThemeChangeListener(this::applyTheme)` to automatically recolor UI elements upon mode change.

---

## 🔧 Common Development Workflows

### How to add a new vector icon
1. Open [UIIcons.java](file:///d:/huy/GiuaKiLapTrinhMang/GiuaKi/src/ui/UIIcons.java).
2. Create a new static method returning an `Icon`:
   ```java
   public static Icon createMyIcon(int size, Color color) {
       return new Icon() {
           @Override
           public void paintIcon(Component c, Graphics g, int x, int y) {
               Graphics2D g2 = (Graphics2D) g.create();
               g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               g2.setColor(color);
               // Draw shapes using g2.draw() / g2.fill()
               g2.dispose();
           }
           @Override public int getIconWidth() { return size; }
           @Override public int getIconHeight() { return size; }
       };
   }
   ```
3. Use `createIconButton(UIIcons.createMyIcon(20, ThemeManager.getAccent()), "Tooltip")` in your UI component.

---

## 🧪 Testing & Build

### Verifying Code Compilation
Run the standard Java compilation command:
```powershell
& "C:\Program Files\Java\jdk-22\bin\javac.exe" -encoding UTF-8 -d bin (Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName)
```
*A clean compilation produces zero stderr output.*

---

## 🚨 Troubleshooting

### 1. `java.net.BindException: Address already in use`
- **Cause**: Server Port 2020 or 2021 is already occupied by another running instance of `TCPServer` or another application.
- **Fix**: Kill existing Java processes before launching:
  ```powershell
  Stop-Process -Name "java" -Force -ErrorAction SilentlyContinue
  ```

### 2. `Ten <username> dang duoc su dung`
- **Cause**: Another client is already logged in with the exact same display name.
- **Fix**: Use a unique username in `LoginDialog`.

### 3. Client cannot connect to Server on another machine in LAN
- **Cause**: Windows Firewall blocking incoming traffic on Ports 2020 / 2021.
- **Fix**: Allow Java binary through Windows Defender Firewall, or run Server & Client using `127.0.0.1` for local testing.

---

## 📖 Glossary

| Term | Definition |
| --- | --- |
| **TCP** | Transmission Control Protocol - Connection-oriented, guaranteed data delivery protocol used for chat text & file transfer. |
| **UDP** | User Datagram Protocol - Connectionless, fast packet protocol used for heartbeat online presence & typing status. |
| **ClientHandler** | Server-side thread dedicated to processing streams for a single connected TCP client. |
| **ThemeManager** | Single-source-of-truth class managing application color tokens and broadcasting theme state changes. |
| **Empty State** | Placeholder screen displayed in `ChatPanel` before any user conversation is selected from the sidebar. |

---

## 📊 Summary of Documentation Verification

- **Documented**: Complete architecture, directory tree, protocol specification, build steps, theme engine details, and troubleshooting guides.
- **Verified**: All paths (`src/client`, `src/server`, `src/ui`), default ports (`2020`, `2021`), compilation commands, and code entry points (`server.TCPServer`, `client.TCPClient`).
- **Secrets & Credentials**: None present in codebase or documentation.
