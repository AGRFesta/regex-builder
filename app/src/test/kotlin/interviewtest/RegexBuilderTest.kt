package interviewtest

import assertk.all
import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import org.junit.Test

class RegexBuilderTest {

    @Test
    fun buildRegex_emptyInputSet_throwsException() {
        assertThat { emptySet<String>().buildRegex() }
            .isFailure()
            .all {
                hasClass(IllegalArgumentException::class)
                hasMessage("Input Set can't be empty")
            }
    }

    @Test
    fun buildRegex_elementStartingWithDigitInputSet_throwsException() {
        assertThat { setOf(
            "AB123ZZ",
            "345",
            "CF678HG")
            .buildRegex() }
            .isFailure()
            .all {
                hasClass(IllegalArgumentException::class)
                hasMessage("First char must be a letter")
            }
    }

    @Test
    fun buildRegex_elementContainingInvalidCharInputSet_throwsException() {
        assertThat { setOf(
            "AB123ZZ",
            "ABC+234",
            "CF678HG")
            .buildRegex() }
            .isFailure()
            .all {
                hasClass(IllegalArgumentException::class)
                hasMessage("Input set contains invalid char: +")
            }
    }

    @Test
    fun buildRegex_elementContainingLowerCaseCharInputSet_throwsException() {
        assertThat { setOf(
            "AB123ZZ",
            "ABCz234",
            "CF678HG")
            .buildRegex() }
            .isFailure()
            .all {
                hasClass(IllegalArgumentException::class)
                hasMessage("Input set contains invalid char: z")
            }
    }

    @Test
    fun buildRegex_emptyElementInputSet_throwsException() {
        assertThat { setOf(
            "AB123ZZ",
            "",
            "CF678HG")
            .buildRegex() }
            .isFailure()
            .all {
                hasClass(IllegalArgumentException::class)
                hasMessage("Input set can't contain empty strings")
            }
    }

    @Test
    fun buildRegex_simpleSet() {
        val regex = setOf(
            "AB123ZZ",
            "BB742TG",
            "CF678HG")
            .buildRegex()

        assertThat(regex).isEqualTo("[A-Z]{2}\\d{3}[A-Z]{2}")
    }

    @Test
    fun buildRegex_optionalGroupSizesSet() {
        val regex = setOf(
            "AB123ZZ",
            "BBH42TG",
            "CF678HG")
            .buildRegex()

        assertThat(regex).isEqualTo("[A-Z]{2,3}\\d{2,3}[A-Z]{2}")
    }

    @Test
    fun buildRegex_singleCharGroupsSet() {
        val regex = setOf(
            "A",
            "B4F4R",
            "C6T")
            .buildRegex()

        assertThat(regex).isEqualTo("[A-Z]\\d{0,1}[A-Z]{0,1}\\d{0,1}[A-Z]{0,1}")
    }

    @Test
    fun buildRegex_singleEntrySet() {
        val regex = setOf("TNTTST80A01F205E")
            .buildRegex()

        assertThat(regex).isEqualTo("[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]")
    }

    @Test
    fun buildRegex_variousLengthsSet() {
        val regex = setOf(
            "AA123",
            "BA1234",
            "AB12345")
            .buildRegex()

        assertThat(regex).isEqualTo("[A-Z]{2}\\d{3,5}")
    }

    @Test
    fun buildRegex_multiOptionalGroupsSet() {
        val regex = setOf(
            "AA123",
            "BA1234HHHHHHHHHT",
            "AB12345FF674A")
            .buildRegex()

        assertThat(regex).isEqualTo("[A-Z]{2}\\d{3,5}[A-Z]{0,10}\\d{0,3}[A-Z]{0,1}")
    }

}