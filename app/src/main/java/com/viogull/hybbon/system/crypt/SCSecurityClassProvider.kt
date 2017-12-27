package com.viogull.hybbon.system.crypt

/**
 * Created by ghost on 27.12.2017.
 */

import org.hive2hive.core.serializer.ISecurityClassProvider
import org.spongycastle.jcajce.provider.asymmetric.rsa.BCRSAPrivateCrtKey
import org.spongycastle.jcajce.provider.asymmetric.rsa.BCRSAPrivateKey
import org.spongycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey
import org.spongycastle.jce.provider.BouncyCastleProvider

import java.security.interfaces.RSAPrivateCrtKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * Created by rsv on 14.05.2017.
 */

class SCSecurityClassProvider : ISecurityClassProvider {

    override fun getSecurityProvider(): String {
        return SECURITY_PROVIDER
    }

    override fun getRSAPublicKeyClass(): Class<out RSAPublicKey> {
        return BCRSAPublicKey::class.java
    }

    override fun getRSAPrivateKeyClass(): Class<out RSAPrivateKey> {
        return BCRSAPrivateKey::class.java
    }

    override fun getRSAPrivateCrtKeyClass(): Class<out RSAPrivateCrtKey> {
        return BCRSAPrivateCrtKey::class.java
    }

    companion object {

        val SECURITY_PROVIDER = BouncyCastleProvider.PROVIDER_NAME
    }
}