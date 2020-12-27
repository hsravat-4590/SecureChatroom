package com.ravat.hanzalah.securechat.net.server

import com.ravat.hanzalah.securechat.net.Packet

data class PacketRecord(val author: String, val packet: Packet.Payload)
