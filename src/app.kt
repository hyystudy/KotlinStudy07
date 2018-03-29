import javafx.geometry.Pos
import java.security.cert.Extension
import java.time.LocalDate

fun main(args: Array<String>) {
    val p1 = Point(10, 20)
    val p2 = Point(30, 40)
    val p3 = Point(3, 2)

    //println(p1 + p2)
    println(p2 - p1 * p3)
    println(p1 * 1.5)
    println('a'.times(4))

    //复合赋值运算符 += -= *= /=
    //如果有plus函数 直接调用 plus 函数  并赋值给左侧变量
    var p4 = Point(50, 60)
    p4 += p1

    val arrayList = ArrayList<Int>()
    arrayList += 42
    arrayList.plusAssign(32)
    //返回一个新的集合 并在集合最后加了一个元素 21 注意并没有修改原有的集合内容 可以查看源码实现得知
    val numbers = arrayList.plus(21)

    println(arrayList[0])
    println(arrayList[1])

    println(numbers[2])

    //可变集合
    val list1 = arrayListOf(1, 2)
    list1 += 3//[1, 2, 3]
    val newList = list1 + numbers//list1:[1, 2, 3] newList:[1, 2, ,3, 42, 32, 21]

    //可读集合
    var list2 = listOf(1, 2)
    list2 += 3

    //比较运算符 equals == !=
    println(Point(19, 19) ==  Point(19, 19))

    println(Point(19, 19) !=  Point(5, 19))

    //排序运算符 compareTo < > <= >=
    // p1 < p2 --> p1.compareTo(p2) < 0
    val person1 = Person("Alice", "Smith")
    val person2 = Person("Bob", "Johnson")
    println(person1 > person2)
    println("Alice" > "Bob")

    //通过下标访问map中的元素 实际调用的是get运算符
    //val value = map[key]
    //通过下标设置map中的元素 实际调用的是set运算符
    //mutable[key] = newValue

    println(p1[0])
    println(p1[1])

    val mutablePoint = MutablePoint(11, 22)
    mutablePoint[0] = 23//set(0, 23)
    println(mutablePoint)

    //in 的约定  a in list  -> list.contains(a)
    val rect = Rectangle(Point(10, 20), Point(50, 50));
    println(Point(20, 30) in rect)
    println(Point(5, 5) in rect)

    //.. 约定 1..10(1到10的数字闭区间) -> 1.rangeTo(10)
    val now = LocalDate.now()
    val vacation = now..now.plusDays(10)
    println(now.plusWeeks(1) in vacation)

    (0..9).forEach { print(it) }
    for (c in "abc"){
        println(c)
    }

    val newYear = LocalDate.ofYearDay(2018, 1)
    val daysOff = newYear.minusDays(2)..newYear
    for (dayOff in daysOff){
        println(dayOff)
    }


    //解构声明 也用到约定的原理 componentN()函数 N代表第几个参数
    val p = Point(10, 20)
    val (x, y) = p
    println(x)
    println(y)

    val (name, ext) = splitFileName("example.kt")
    println(name)
    println(ext)

    //使用解构声明 来遍历集合
    val  map = mapOf("Oracle" to "Java", "JetBrains" to "Kotlin")
    printEntries(map)
}

fun printEntries(map: Map<String, String>) {
    for ((key, value) in map){
        println("$key --> $value")
    }
}


operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> =
        object  : Iterator<LocalDate> {
            var current = start

            override fun hasNext(): Boolean = current <= endInclusive

            override fun next(): LocalDate =
                current.apply { current = plusDays(1) }

        }


//如果class前面加上了data 代表的是数据类 component1() component2()已经自动帮助生成了
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }

   // operator fun component1() = x

  /*  override fun equals(other: Any?): Boolean {
        if(other === this) return true //恒等运算符 检查参数与this是否是同一个对象
        if (other !is Point) return false //检查参数类型
        return other.x == x && other.y == y
    }*/

}

data class NameComponents(val name: String,
                          val extension: String)

fun splitFileName(fullName: String): NameComponents {

    val result = fullName.split('.', limit = 2)
    val (name, ext) = fullName.split('.', limit = 2)
    return NameComponents(name, ext)
    //return NameComponents(result[0], result[1])
}

data class Rectangle(val upperLeft: Point, val lowerRight: Point)

operator fun Rectangle.contains(p: Point): Boolean {
    return p.x in upperLeft.x until lowerRight.x &&
           p.y in upperLeft.y until lowerRight.y
}

//实现get约定(重载get方法) 可以达到 p[0] = x    p[1] = y
operator fun Point.get(index: Int) = when(index){
    0 -> x
    1 -> y
    else ->
            throw IndexOutOfBoundsException("Invalid coordinate $index")
}

//定义一个可变的Point
data class MutablePoint(var x: Int, var y: Int)

//重载MutablePoint的set方法 可以在赋值语句中使用下标运算符 来更改mutablePoint的属性
operator fun MutablePoint.set(index: Int, value: Int){
    when(index) {
        0 -> x = value
        1 -> y = value
        else ->
                throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}

operator fun Point.minus(other: Point): Point {
    return Point(x - other.x, y - other.y)
}

operator fun Point.times(other: Point): Point {
    return Point(x * other.x, y * other.y)
}

//运算数类型不同的运算符 不支持交换性 可以Point * 1.5 不可以 1.5 * Point 除非定义Double.times(p: Point)函数
operator fun Point.times(scale: Double): Point {
    return Point((x * scale).toInt(), (y * scale).toInt())
}

//返回类型不同于任一运算数类型
operator fun Char.times(count: Int): String {
    return toString().repeat(count)
}

//一元运算符 -a +b ++a(先加在返回) a++(先返回a再执行++) --a a--
operator fun Point.unaryMinus(): Point {
    return Point(-x, -y)
}


class Person(
        val firstName: String, val lastName: String
) : Comparable<Person> {
    override fun compareTo(other: Person): Int {
        //比较俩个person 先比较firstName 然后是LastName
        return compareValuesBy(this, other, Person::firstName, Person::lastName)
//        return compareValuesBy(this, other, { it.firstName },  { it.lastName })
    }
}