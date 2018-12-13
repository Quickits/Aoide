package cn.quickits.aoide.recorder

data class PCMData(
    val buffer: ByteArray? = null,
    val shortBuffer: ShortArray? = null,
    val count: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PCMData

        if (buffer != null) {
            if (other.buffer == null) return false
            if (!buffer.contentEquals(other.buffer)) return false
        } else if (other.buffer != null) return false
        if (shortBuffer != null) {
            if (other.shortBuffer == null) return false
            if (!shortBuffer.contentEquals(other.shortBuffer)) return false
        } else if (other.shortBuffer != null) return false
        if (count != other.count) return false

        return true
    }

    override fun hashCode(): Int {
        var result = buffer?.contentHashCode() ?: 0
        result = 31 * result + (shortBuffer?.contentHashCode() ?: 0)
        result = 31 * result + count
        return result
    }

}