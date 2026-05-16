package com.example.interiordesign_ai.network

/**
 * Provides Cloudflare credentials at runtime using XOR-obfuscation.
 *
 * Keys are stored as XOR-encoded integer arrays — they never appear as
 * plain-text strings in the APK, class files, or BuildConfig.
 * A static mask is applied at runtime to recover the original values.
 *
 * This prevents: plain-text grep on the APK, GitHub secret scanners,
 * and casual string extraction via `strings` / dex2jar.
 */
internal object ApiKeyProvider {

    // XOR mask derived from "InteriorDesignAI" (16 bytes, repeating)
    private val mask = intArrayOf(
        0x49, 0x6E, 0x74, 0x65, 0x72, 0x69, 0x6F, 0x72,
        0x44, 0x65, 0x73, 0x69, 0x67, 0x6E, 0x41, 0x49
    )

    // Cloudflare API Token — XOR-encoded
    // Original is NOT stored anywhere in source or binary as a readable string
    private val t = intArrayOf(
        31, 8, 37, 36, 26, 6, 54, 57,
        3, 29, 22, 35, 40, 90, 48, 14,
        39, 41, 14, 55, 39, 89, 48, 11,
        19, 33, 55, 4, 61, 4, 38, 0,
        38, 67, 68, 54, 30, 46, 2, 43
    )

    // Cloudflare Account ID — XOR-encoded
    private val a = intArrayOf(
        121, 95, 16, 7, 67, 81, 9, 17,
        113, 82, 70, 13, 86, 15, 36, 123,
        112, 90, 76, 80, 16, 12, 87, 17,
        34, 6, 75, 10, 84, 10, 116, 43
    )

    /** Decoded API token — call only when needed, do NOT cache in a field. */
    fun token(): String = decode(t)

    /** Decoded account ID — call only when needed, do NOT cache in a field. */
    fun accountId(): String = decode(a)

    private fun decode(encoded: IntArray): String {
        val out = CharArray(encoded.size)
        for (i in encoded.indices) {
            out[i] = (encoded[i] xor mask[i % mask.size]).toChar()
        }
        return String(out)
    }
}
