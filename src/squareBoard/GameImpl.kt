package squareBoard

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl(width)
fun <T> createGameBoard(width: Int): GameBoard<T> =
    GameBoardImpl(createSquareBoard(width))

private class SquareBoardImpl(override val width: Int) : SquareBoard {

    private val board: Map<Pair<Int, Int>, Cell> =
        (1..width).flatMap { i ->
            (1..width).map { j -> (i to j) to Cell(i, j) }
        }.toMap()

    override fun getCellOrNull(i: Int, j: Int): Cell? = board[i to j]

    override fun getCell(i: Int, j: Int): Cell =
        getCellOrNull(i, j) ?: throw IllegalArgumentException("$i and $j should be between 0 and ${width - 1}")

    override fun getAllCells(): Collection<Cell> = board.values

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> =
        jRange.map { j -> i to j }.mapNotNull(board::get)

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> =
        iRange.map { i -> i to j }.mapNotNull(board::get)

    override fun Cell.getNeighbour(direction: Direction): Cell? = when (direction) {
        Direction.DOWN -> board[i + 1 to j]
        Direction.UP -> board[i - 1 to j]
        Direction.RIGHT -> board[i to j + 1]
        Direction.LEFT -> board[i to j - 1]
    }
}

private class GameBoardImpl<T>(board: SquareBoard) :
    GameBoard<T>, SquareBoard by board {

    private val cellToValueMap: MutableMap<Cell, T?> =
        getAllCells().associateWithTo(mutableMapOf(), { null })

    private val cellValues: MutableCollection<T?>
        get() = cellToValueMap.values

    override fun get(cell: Cell): T? = cellToValueMap[cell]

    override fun set(cell: Cell, value: T?) {
        cellToValueMap[cell] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> =
        cellToValueMap.filterValues(predicate).keys

    override fun find(predicate: (T?) -> Boolean): Cell? = filter(predicate).first()

    override fun any(predicate: (T?) -> Boolean): Boolean = cellValues.any(predicate)

    override fun all(predicate: (T?) -> Boolean): Boolean = cellValues.all(predicate)
}