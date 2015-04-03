public class Test {
    /**
     * @param args the command line arguments
     */
    public static void main (String[] args) {
        Player player = new Player();
        for (String resource : GameController.RESOURCE_TYPES) {
            player.giveResource(resource, 2);
        }
        player.giveDevCard(new DevelopmentCard("Knight"));
        player.giveDevCard(new DevelopmentCard("Year of Plenty"));
        player.giveDevCard(new DevelopmentCard("Monopoly"));
        player.giveDevCard(new DevelopmentCard("Chapel"));
        player.giveDevCard(new DevelopmentCard("Road Building"));
        player.giveDevCard(new DevelopmentCard("Knight"));
        player.giveDevCard(new DevelopmentCard("University"));
        player.giveDevCard(new DevelopmentCard("Knight"));
        CardsFrame cardsFrame = new CardsFrame(new GameIcons(), player);
    }
}
