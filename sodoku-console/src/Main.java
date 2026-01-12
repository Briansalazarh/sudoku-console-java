package src;

import src.model.Board;
import src.model.Space;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import static src.util.BoardTemplate.BOARD_TEMPLATE;

public class Main {

    private final static Scanner scanner = new Scanner(System.in);
    private static Board board;
    private final static int BOARD_LIMIT = 9;

    public static void main(String[] args) {
        // Esta parte procesa los argumentos de entrada para crear el mapa del tablero
        // Formato esperado de args: "fila,columna;valorEsperado,esFija"
        final var positions = Stream.of(args)
                .collect(toMap(
                        k -> k.split(";")[0], // Clave: "fila,columna"
                        v -> v.split(";")[1]  // Valor: "valorEsperado,esFija"
                ));
        var option = -1;
        while (true){
            System.out.println("Seleccione una de las siguientes opciones");
            System.out.println("1 - Iniciar un nuevo Juego");
            System.out.println("2 - Colocar un número");
            System.out.println("3 - Remover un número");
            System.out.println("4 - Visualizar juego actual");
            System.out.println("5 - Verificar estado del juego");
            System.out.println("6 - Limpiar juego");
            System.out.println("7 - Finalizar juego");
            System.out.println("8 - Salir");

            option = scanner.nextInt();

            switch (option){
                case 1 -> startGame(positions);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> showCurrentGame();
                case 5 -> showGameStatus();
                case 6 -> clearGame();
                case 7 -> finishGame();
                case 8 -> System.exit(0);
                default -> System.out.println("Opción inválida, seleccione una de las opciones del menú");
            }
        }
    }

    private static void startGame(final Map<String, String> positions) {
        if (nonNull(board)){
            System.out.println("El juego ya ha sido iniciado");
            return;
        }

        List<List<Space>> spaces = new ArrayList<>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_LIMIT; j++) {
                // Aquí busca la configuración para la posición i,j
                var positionConfig = positions.get("%s,%s".formatted(i, j));
                var expected = Integer.parseInt(positionConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                var currentSpace = new Space(expected, fixed);
                spaces.get(i).add(currentSpace);
            }
        }

        board = new Board(spaces);
        System.out.println("El juego está listo para comenzar");
    }

    private static void inputNumber() {
        if (isNull(board)){
            System.out.println("El juego aún no ha sido iniciado");
            return;
        }

        System.out.println("Informe la columna en la que se insertará el número (0-8)");
        var col = runUntilGetValidNumber(0, 8);
        System.out.println("Informe la fila en la que se insertará el número (0-8)");
        var row = runUntilGetValidNumber(0, 8);
        System.out.printf("Informe el número que va a entrar en la posición [%s,%s]\n", col, row);
        var value = runUntilGetValidNumber(1, 9);

        // Intentamos cambiar el valor. Si devuelve false, es porque era una casilla fija
        if (!board.changeValue(col, row, value)){
            System.out.printf("La posición [%s,%s] tiene un valor fijo y no se puede cambiar\n", col, row);
        }
    }

    private static void removeNumber() {
        if (isNull(board)){
            System.out.println("El juego aún no ha sido iniciado");
            return;
        }

        System.out.println("Informe la columna del número a remover");
        var col = runUntilGetValidNumber(0, 8);
        System.out.println("Informe la fila del número a remover");
        var row = runUntilGetValidNumber(0, 8);
        if (!board.clearValue(col, row)){
            System.out.printf("La posición [%s,%s] tiene un valor fijo\n", col, row);
        }
    }

    private static void showCurrentGame() {
        if (isNull(board)){
            System.out.println("El juego aún no ha sido iniciado");
            return;
        }

        var args = new Object[81];
        var argPos = 0;
        // Esta lógica transforma el tablero (matriz) en un array plano para inyectarlo en el String template
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (var col: board.getSpaces()){
                args[argPos ++] = " " + ((isNull(col.get(i).getActual())) ? " " : col.get(i).getActual());
            }
        }
        System.out.println("Tu juego se encuentra de la siguiente forma:");
        System.out.printf((BOARD_TEMPLATE) + "\n", args);
    }

    private static void showGameStatus() {
        if (isNull(board)){
            System.out.println("El juego aún no ha sido iniciado");
            return;
        }

        System.out.printf("El juego actualmente se encuentra en estado: %s\n", board.getStatus().getLabel());
        if(board.hasErrors()){
            System.out.println("El juego contiene errores");
        } else {
            System.out.println("El juego no contiene errores");
        }
    }

    private static void clearGame() {
        if (isNull(board)){
            System.out.println("El juego aún no ha sido iniciado");
            return;
        }

        System.out.println("¿Estás seguro que deseas limpiar tu juego y perder todo el progreso?");
        var confirm = scanner.next();
        while (!confirm.equalsIgnoreCase("si") && !confirm.equalsIgnoreCase("no")){
            System.out.println("Informe 'si' o 'no'");
            confirm = scanner.next();
        }

        if(confirm.equalsIgnoreCase("si")){
            board.reset();
        }
    }

    private static void finishGame() {
        if (isNull(board)){
            System.out.println("El juego aún no ha sido iniciado");
            return;
        }

        if (board.gameIsFinished()){
            System.out.println("¡Felicitaciones! Has concluido el juego");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println("Tu juego contiene errores, verifica tu tablero y ajústalo");
        } else {
            System.out.println("Aún necesitas rellenar algún espacio");
        }
    }

    private static int runUntilGetValidNumber(final int min, final int max){
        var current = scanner.nextInt();
        while (current < min || current > max){
            System.out.printf("Informe un número entre %s y %s\n", min, max);
            current = scanner.nextInt();
        }
        return current;
    }
}
