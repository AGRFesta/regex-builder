package interviewtest

import interviewtest.Group.Companion.createInitialGroup
import interviewtest.Group.Companion.createNewGroup
import interviewtest.Group.Companion.noneGroup
import interviewtest.ParsedInput.Companion.parse

fun Set<String>.buildRegex(): String {
    require(isNotEmpty()) {"Input Set can't be empty"}

    return map { parse(it) }
        .reduce { acc, elem -> acc + elem }
        .toString()
}

private enum class GroupType(val matches: (Char) -> Boolean) {
    DIGIT({it.isDigit()}), LETTER({it.isUpperCase()}), NONE({false});

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
        else "{$mandatory${optional?.let {",$optional"} ?: ""}}"

    private fun maxChars() = optional ?: mandatory

    operator fun plus(other: Group): Group {
        val mandatory = minOf(this.mandatory, other.mandatory)
        val optional = maxOf(this.maxChars(), other.maxChars())
            .let { if (it <= mandatory) null else it }
        return Group(other.type + this.type, mandatory, optional)
    }
}

private class ParsedInput private constructor(private val groups: List<Group>): List<Group> by groups {
    companion object {
        fun parse(string: String): ParsedInput {
            require(string.isNotEmpty()) {"Input set can't contain empty strings"}
            require(string[0].isLetter()) {"First char must be a letter"}
            var actualGroup = createInitialGroup()
            val groupList = mutableListOf(actualGroup)
            string.forEach {
                if (actualGroup.type.matches(it)) {
                    actualGroup.increase()
                } else {
                    actualGroup = createNewGroup(it.getGroupType())
                    groupList.add(actualGroup)
                }
            }
            return ParsedInput(groupList)
        }
    }

    operator fun plus(other: ParsedInput): ParsedInput {
        val mergedGroups = (0 until maxOf(this.size, other.size))
            .map { this.getGroupOrNone(it) + other.getGroupOrNone(it) }
        return ParsedInput(mergedGroups)
    }

    private fun List<Group>.getGroupOrNone(position: Int): Group = if (position >= size) noneGroup else this[position]

    override fun toString(): String = groups.joinToString(separator = "") { it.toString() }
}
