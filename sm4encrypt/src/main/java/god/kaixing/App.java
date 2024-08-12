package god.kaixing;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        testEncrypt();
    }

    public static void testEncrypt() {
        SM4EncryptUDF sm4EncryptUDF = new SM4EncryptUDF();
        
        String key;
        key = "11111111111111111111111111111111fsvgsvgsdvsb hj jhb jhkbyvhjv nm hvjkjmn k hvkviyv hivyftyfv vtftfycvyg"; // 16字节密钥
        String plainText = "Hello, World!";
        
        String encryptedText = sm4EncryptUDF.evaluate(plainText, key);
        
        System.out.println("Encrypted text: " + encryptedText);
    }
}
