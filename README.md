# 构建UDF的jar包示例
1. 安装java，安装maven
2. 构建工程
    
    ```bash
    mvn archetype:generate -DgroupId=god.kaixing -DartifactId=sm4encrypt -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
    ```
    
3. 在sm4encrypt/src/main/java/god/kaixing/SM4EncryptUDF.java中编写代码
    
    ```java
    import org.apache.hadoop.hive.ql.exec.UDF;
    import org.bouncycastle.jce.provider.BouncyCastleProvider;
    
    import javax.crypto.Cipher;
    import javax.crypto.spec.IvParameterSpec;
    import javax.crypto.spec.SecretKeySpec;
    import java.security.SecureRandom;
    import java.security.Security;
    
    public class SM4EncryptUDF extends UDF {
        // 在静态代码块中添加BouncyCastle安全提供者
        static {
            Security.addProvider(new BouncyCastleProvider());
        }
    
        /**
         * 加密方法
         * @param plainText 待加密的明文
         * @param key 加密密钥
         * @return 加密后的密文（以十六进制字符串表示）
         */
        public String evaluate(String plainText, String key) {
            // 检查明文和密钥是否为空
            if (plainText == null || key == null) {
                return null;
            }
    
            try {
                // 初始化密钥和IV的字节数组
                byte[] keyBytes = new byte[16];
                byte[] ivBytes = new byte[16];
    
                // 将密钥转换为字节数组，并检查其长度是否至少为16字节
                byte[] keyByteArray = key.getBytes("UTF-8");
                if (keyByteArray.length < 16) {
                    throw new IllegalArgumentException("Key length should be at least 16 bytes");
                }
                // 将密钥字节数组复制到keyBytes中
                System.arraycopy(keyByteArray, 0, keyBytes, 0, 16);
    
                // 使用SecureRandom生成随机的IV
                SecureRandom random = new SecureRandom();
                random.nextBytes(ivBytes);
    
                // 使用密钥字节数组创建SecretKeySpec对象
                SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "SM4");
                // 使用IV字节数组创建IvParameterSpec对象
                IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
    
                // 获取SM4/CBC/PKCS7Padding加密算法的Cipher实例
                Cipher cipher = Cipher.getInstance("SM4/CBC/PKCS7Padding", "BC");
                // 用密钥和IV初始化Cipher对象
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
    
                // 将明文加密为字节数组
                byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
                // 将加密后的字节数组转换为十六进制字符串并返回
                return bytesToHex(encrypted);
            } catch (Exception e) {
                // 打印异常堆栈跟踪信息并返回null
                e.printStackTrace();
                return null;
            }
        }
    
        /**
         * 将字节数组转换为十六进制字符串
         * @param bytes 待转换的字节数组
         * @return 转换后的十六进制字符串
         */
        private String bytesToHex(byte[] bytes) {
            // 创建StringBuilder对象用于构建十六进制字符串
            StringBuilder hexString = new StringBuilder();
            // 遍历字节数组
            for (byte b : bytes) {
                // 将每个字节转换为十六进制字符串
                String hex = Integer.toHexString(0xFF & b);
                // 如果十六进制字符串的长度为1，则在前面补0
                if (hex.length() == 1) hexString.append('0');
                // 将十六进制字符串追加到StringBuilder对象中
                hexString.append(hex);
            }
            // 返回构建好的十六进制字符串
            return hexString.toString();
        }
    }
    
    ```
    
4. 在pom.xml中添加依赖
    
    ```xml
    <dependencies>
        <!-- Bouncy Castle 加密库 -->
        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcprov-jdk15on</artifactId>
            <version>1.68</version>
        </dependency>
        <!-- Hive 依赖 -->
        <dependency>
            <groupId>org.apache.hive</groupId>
            <artifactId>hive-exec</artifactId>
            <version>3.1.2</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
    
    ```
    
5. 进入工程，输入指令编译成jar包
    
    ```bash
    mvn clean package
    ```
