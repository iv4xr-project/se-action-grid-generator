package generator;

public class MainApp {

    //example of action grid generation and making use of it in our game simulator
    public static void main(String[] args) {
        int numberOfPlayers = 2;

        //example of values used, can be changed however beware of long computational times for large dimensions
        int numberOfBugs = 15;
        int gameMapDimension = 10;
        int numberOfBlocks = 20;
        int actionGridDimension = 5;
        int sequenceSize = 5;

        //instantiate and generate a game map
        GameMapGenerator gameMapGenerator = new GameMapGenerator(gameMapDimension, gameMapDimension, numberOfPlayers);
        gameMapGenerator.generate(numberOfBlocks);

        //instantiate the game simulator by generating the bugs for the game map
        GameSimulator gameSimulator = new GameSimulator(gameMapGenerator, numberOfBugs);


        /* uncomment in order to have the game map interface be shown

        List<Entity> entityList = new ArrayList<>(gameMapGenerator.getResponseEntities().values());
        List<BlockEntity> interactableBlocks = new ArrayList<>(gameMapGenerator.getReachableEntities().values());
        Grid application = new Grid(entityList, interactableBlocks);
        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        */


        //instantiate the action grid generator and generate action sequence pairs to fill the action grid
        ActionGridGenerator actionGridGenerator =
                new ActionGridGenerator(numberOfPlayers, actionGridDimension, gameMapGenerator);
        actionGridGenerator.fillActionGrid(sequenceSize);


        /* uncomment if you wish to have the action grid printed on the terminal
        (depending on the dimensions it may be very hard to read)

        System.out.println(actionGridGenerator.getMapElite().toString());
         */


        //calculate diversity and show the results on the terminal
        DiversityCalculator calculator = new DiversityCalculator();
        System.out.println("Calculated diversity: " +
                calculator.calculateGridDiversity(actionGridGenerator.getMapElite()));
        System.out.println("Least diverse match: " + (1 - calculator.maxTotalSimularity));


        //simulate the action sequence pairs in the grid on the game simulator and see how many bugs were detected
        gameSimulator.simulate(actionGridGenerator.getMapElite(), true);
        int bugCount = gameSimulator.connectedBlocksBugsDetected.size() +
                gameSimulator.concurrentBlockBugsDetected.size() +
                gameSimulator.singularBlockBugsDetected.size();
        System.out.println("Diverse\n Bugs detected: " + bugCount +
                " " + gameSimulator.singularBlockBugsDetected.size() +
                " " + gameSimulator.concurrentBlockBugsDetected.size() +
                " " + gameSimulator.connectedBlocksBugsDetected.size());
    }
}
