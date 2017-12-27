package com.viogull.hybbon.system.crypt

/**
 * Created by ghost on 27.12.2017.
 */
import org.hive2hive.core.security.H2HDefaultEncryption
import org.hive2hive.core.serializer.IH2HSerialize

import java.security.Security

class SpongyCastleEncryption(serializer: IH2HSerialize) : H2HDefaultEncryption(serializer, SCSecurityClassProvider.SECURITY_PROVIDER, SCStrongAESEncryption()) {


    init {

        // install the SC provider instead of the BC provider
        if (Security.getProvider(SCSecurityClassProvider.SECURITY_PROVIDER) == null) {
            Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider(), 1)
        }
    }
}