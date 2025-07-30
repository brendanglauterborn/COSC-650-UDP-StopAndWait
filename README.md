# COSC 650 Socket Programming

A course project that simulates a client and a server sending datagrams to each other.

> I use `devenv` to take care of all the listed requirements. Install [nix](https://determinate.systems/nix-installer/) 😈

## Requirements

- [just](https://github.com/casey/just): Makefile alternative
- openjdk 21.0.4

## Instructions

All messages between the client `C` and the server `S` are carried over UDP.

Some messages may be received out of order, and some may be lost. 

The messages sent by the server `S` to the client `C` must have the following three fields:

- **Sequence number**: int (sequence numbers alternate as 0, 1, 0, 1, …)
- **Payload Length**: int (size of the payload in bytes)
- **Payload**: data bytes in packet (maximum size of the payload is 1024 bytes)

The server `S` runs as localhost and uses port 11122. 

`S` first asks the user to enter a timeout period ts in seconds.

The client `C` asks the user to enter a string s with the name of a Web server W in the form: `www.name.suf` (for example, `s=www.towson.edu`).

It then sends a message to `S` that has the bytes of the string `s` as payload.

When the server `S` gets the client request, it starts a separate handler (thread) for the client `C`. 

The server `S` does all the client-related processing and communication using the handler.

The main thread in the server `S` only listens for client requests.

`S` sends a GET request to the Web server `W` over HTTPS using `HttpURLConnection`.
`S` stores all the data received from W in memory.
`S` then sends messages with the data to `C`.

Only one message is sent at a time and the server waits for the `ACK` before sending the next message (as in the STOP and WAIT protocol).

The payload in each data message from `S` to `C` carries 1024 bytes of data from `W` except for the payload in the last data message that carries the remaining bytes of data from `W`.

`S` then starts the timer for the timeout period ts.

It waits for a message from `C` whose payload is the `ACK` number 0 or 1 (an int).

Before the timeout ts, if `S` gets the ACK, it transmits the next message. Otherwise, it retransmits the message.

### Running the project

> Server asks too many questions

Refer to the `justfile`

- `just default`: Compiles and runs both the server and client programs
- `just compile`: Compiles both the server and client in the `bin` dir
- `just server`: Compiles the server in the `bin` dir
- `just client`: Compiles the client in the `bin` dir
- `just clean`: Cleans up the `bin` dir

## Contributors
Brendan Lauterborn
Luis Gascon
