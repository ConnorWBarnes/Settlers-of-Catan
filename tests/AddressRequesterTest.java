import soc.base.net.AddressRequester;

/**
 * Tests the AddressRequester class by creating an instance of it with an
 * AddressListener that prints out the address that is received.
 * @author Connor Barnes
 */
public class AddressRequesterTest {
    public static void main(String[] args) {
        new AddressRequester(new AddressRequester.AddressListener() {
            @Override
            public void addressEntered(String address) {
                System.out.println("Address: " + address);
            }
        });
    }
}
