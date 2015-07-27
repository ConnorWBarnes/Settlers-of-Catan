import soc.base.ConnectionInfoRequester;

/**
 * Tests the ConnectionInfoRequester class by creating an instance of it with a
 * ConnectionInfoListener that prints out the info that is received.
 * @author Connor Barnes
 */
public class ConnectionInfoRequesterTest {
    public static void main(String[] args) {
        new ConnectionInfoRequester(new ConnectionInfoRequester.ConnectionInfoListener() {
            @Override
            public void connectionInfoEntered(String infoEntered) {
                System.out.println(infoEntered);
            }
        });
    }
}
