package com.viogull.hybbon.system.crypt

/**
 * Created by ghost on 27.12.2017.
 */
import org.hive2hive.core.security.IStrongAESEncryption
import org.spongycastle.crypto.DataLengthException
import org.spongycastle.crypto.InvalidCipherTextException
import org.spongycastle.crypto.engines.AESEngine
import org.spongycastle.crypto.modes.CBCBlockCipher
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.spongycastle.crypto.params.KeyParameter
import org.spongycastle.crypto.params.ParametersWithIV

import java.security.GeneralSecurityException

import javax.crypto.SecretKey

/**
 * Copy of [org.hive2hive.core.security.BCStrongAESEncryption], but using spongy castle
 *
 * @author Nico
 */
class SCStrongAESEncryption : IStrongAESEncryption {

    @Throws(GeneralSecurityException::class)
    override fun encryptStrongAES(data: ByteArray, key: SecretKey, initVector: ByteArray): ByteArray {
        try {
            return processAESCipher(true, data, key, initVector)
        } catch (e: DataLengthException) {
            throw GeneralSecurityException("Cannot encrypt the data with AES 256bit", e)
        } catch (e: IllegalStateException) {
            throw GeneralSecurityException("Cannot encrypt the data with AES 256bit", e)
        } catch (e: InvalidCipherTextException) {
            throw GeneralSecurityException("Cannot encrypt the data with AES 256bit", e)
        }

    }

    @Throws(GeneralSecurityException::class)
    override fun decryptStrongAES(data: ByteArray, key: SecretKey, initVector: ByteArray): ByteArray {
        try {
            return processAESCipher(false, data, key, initVector)
        } catch (e: DataLengthException) {
            throw GeneralSecurityException("Cannot decrypt the data with AES 256bit", e)
        } catch (e: IllegalStateException) {
            throw GeneralSecurityException("Cannot decrypt the data with AES 256bit", e)
        } catch (e: InvalidCipherTextException) {
            throw GeneralSecurityException("Cannot decrypt the data with AES 256bit", e)
        }

    }

    @Throws(DataLengthException::class, IllegalStateException::class, InvalidCipherTextException::class)
    private fun processAESCipher(encrypt: Boolean, data: ByteArray, key: SecretKey, initVector: ByteArray): ByteArray {
        // seat up engine, block cipher mode and padding
        val aesEngine = AESEngine()
        val cbc = CBCBlockCipher(aesEngine)
        val cipher = PaddedBufferedBlockCipher(cbc)

        // apply parameters
        val parameters = ParametersWithIV(KeyParameter(key.encoded), initVector)
        cipher.init(encrypt, parameters)

        // process ciphering
        val output = ByteArray(cipher.getOutputSize(data.size))

        val bytesProcessed1 = cipher.processBytes(data, 0, data.size, output, 0)
        val bytesProcessed2 = cipher.doFinal(output, bytesProcessed1)
        val result = ByteArray(bytesProcessed1 + bytesProcessed2)
        System.arraycopy(output, 0, result, 0, result.size)
        return result
    }
}