import com.github.mrpowers.spark.fast.tests.DataFrameComparer
import org.scalatest.BeforeAndAfter
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.scalatest.flatspec.AnyFlatSpec
import SparkWithTest._

class TestUtils extends AnyFlatSpec  with SparkSessionTestWrapper
  with BeforeAndAfter with DataFrameComparer {
  import spark.implicits._

  var testDf: DataFrame = _
  before {
    testDf = spark.read.json("src/test/resources/data/countries.json")
  }

  it should "print correct schema getCountriesWithManyBorders and check not empty result" in {

    val expectedDf = Seq(58L).toDF("count(1)")
    getCountriesWithManyBorders(testDf).printSchema()
    val resultDf = getCountriesWithManyBorders(testDf)
      .agg(count("*"))

    assertSmallDatasetEquality(expectedDf, resultDf, orderedComparison = false)

  }

  it should "print correct schema getLanguagesRating and check english language" in {
    val expectedDf = Seq(1L).toDF("count(1)")

    getLanguagesRating(testDf).printSchema()

    val resultDf = getLanguagesRating(testDf)
      .filter("Language == 'English'")
      .agg(count("*"))

    assertSmallDatasetEquality(expectedDf, resultDf, orderedComparison = false)
  }

  it should "return correct top3 countries list" in {

    val data = Seq(
      ("China", 15),
      ("Russia", 14),
      ("Brazil", 10)
    )

    val expectedDf = data.toDF("Country", "NumBorders")

    val resultDf = getCountriesWithManyBorders(testDf)
      .orderBy(desc("NumBorders"))
      .drop("BorderCountries")
      .limit(3)

    assertSmallDatasetEquality(expectedDf, resultDf, orderedComparison = false)

  }

}
