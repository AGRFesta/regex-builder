package interviewtest

import interviewtest.Group.Companion.createInitialGroup
import interviewtest.Group.Companion.createNewGroup
import interviewtest.Group.Companion.noneGroup

fun Set<String>.buildRegex(): String {
    require(isNotEmpty()) {"Input Set can't be empty"}

    return map { it.toGroupList() }
        .reduce { acc, elem -> acc + elem }
        .joinToString(separator = "") { it.toString() }
}

private enum class GroupType(val matches: (Char) -> Boolean) {
    DIGIT({it.isDigit()}), LETTER({it.isLetter()}), NONE({false});

    operator fun plus(other: GroupType): GroupType = when {
            this == NONE -> other
            other == NONE || this == other -> this
            else -> throw IllegalArgumentException("Input set contains invalid format entry")
        }
}

private fun Char.getGroupType(): GroupType = when {
    GroupType.LETTER.matches(this) -> GroupType.LETTER
    GroupType.DIGIT.matches(this) -> GroupType.DIGIT
    else -> throw IllegalArgumentException("Input set contains invalid char: $this")
}

private class Group private constructor(
    val type: GroupType,
    private var mandatory: Int = 0,
    private var optional: Int? = null
) {
    companion object {
        val noneGroup = Group(GroupType.NONE)
        fun createInitialGroup() = Group(GroupType.LETTER)
        fun createNewGroup(type: GroupType) = Group(type = type, mandatory = 1)
    }
    fun increase() { mandatory++ }
    override fun toString() = if (type == GroupType.LETTER) "[A-Z]${sizesToString()}" else "\\d${sizesToString()}"
    private fun sizesToString() = if (mandatory == 1 && optional == null) ""
        else "{$mandatory${if (optional != null) ",$optional" else ""}}"

    private fun maxChars() = optional ?: mandatory

    operator fun plus(other: Group): Group {
        val mandatory = minOf(this.mandatory, other.mandatory)
        val optional = maxOf(this.maxChars(), other.maxChars())
            .let { if (it <= mandatory) null else it }
        return Group(other.type + this.type, mandatory, optional)
    }
}

private fun String.toGroupList(): List<Group> {
    require(isNotEmpty()) {"Input set can't contain empty strings"}
    require(this[0].isLetter()) {"First char must be a letter"}
    var actualGroup = createInitialGroup()
    val groupList = mutableListOf(actualGroup)
    forEach {
        require(!it.isLowerCase()) {"Input set contains invalid char: $it"}
        if (actualGroup.type.matches(it)) {
            actualGroup.increase()
        } else {
            actualGroup = createNewGroup(it.getGroupType())
            groupList.add(actualGroup)
        }
    }
    return groupList
}

private operator fun List<Group>.plus(other: List<Group>): List<Group> =
    (0 until maxOf(this.size, other.size))
        .map { this.getGroupOrNone(it) + other.getGroupOrNone(it) }

private fun List<Group>.getGroupOrNone(position: Int): Group = if (position >= size) noneGroup else this[position]