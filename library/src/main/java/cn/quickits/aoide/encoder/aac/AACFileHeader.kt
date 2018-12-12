package cn.quickits.aoide.encoder.aac

object AACFileHeader {

    fun writeADTSHeader(packet: ByteArray, packetLen: Int) {
        val profile = 2  //AAC LC
        val freqIdx = 4  //44.1KHz
        val chanCfg = 2  //CPE
        packet[0] = 0xFF.toByte()
        packet[1] = 0xF9.toByte()
        packet[2] = ((profile - 1 shl 6) + (freqIdx shl 2) + (chanCfg shr 2)).toByte()
        packet[3] = ((chanCfg and 3 shl 6) + (packetLen shr 11)).toByte()
        packet[4] = (packetLen and 0x7FF shr 3).toByte()
        packet[5] = ((packetLen and 7 shl 5) + 0x1F).toByte()
        packet[6] = 0xFC.toByte()
    }

}