import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import java.util.Map;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class DemoShopTests {
    Map<String, String> cookie;
    String email = "123123@qw.qw";
    String pass = "123123";
    String item = "31";

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "http://demowebshop.tricentis.com";
        Configuration.baseUrl = "http://demowebshop.tricentis.com";
    }

    @AfterEach
    public void afterEach() {
        open("/cart");
        $("input[name=removefromcart]").click();
        $("input[name=updatecart]").click();
        $(".cart-qty").shouldHave(text("(0)"));
    }

    @Test
    @DisplayName("Добавляем товар в корзину и проверяем")
    public void wishListTest() {
        getCookie();
        checkLogin();
        $(".cart-qty").shouldHave(text("(0)"));
        addItemToBasket();
        refresh();
        $(".cart-qty").shouldHave(text("(1)"));
    }


    public void getCookie() {
        cookie = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("Email", email)
                .formParam("Password", pass)
                .post("/login")
                .then()
                .statusCode(302)
                .extract().cookies();
    }

    public void checkLogin() {
        open("/Themes/DefaultClean/Content/images/logo.png");
        getWebDriver().manage().addCookie(new Cookie("NOPCOMMERCE.AUTH", cookie.get("NOPCOMMERCE.AUTH")));
        open("");
        $(".account").shouldHave(text(email));
    }

    public void addItemToBasket() {
        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .cookie("NOPCOMMERCE.AUTH=" + cookie.get("NOPCOMMERCE.AUTH") + ";")
                .body("addtocart_" + item + ".EnteredQuantity=1")
                .post("/addproducttocart/details/" + item + "/1")
                .then()
                .statusCode(200)
                .body("success", is(true));
    }
}
