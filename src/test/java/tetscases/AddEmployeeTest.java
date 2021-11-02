package tetscases;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.AddEmployeePage;
import pages.DashBoardPage;
import pages.EmployeeListPage;
import pages.LoginPage;
import utils.CommonMethods;
import utils.ConfigReader;
import utils.Constants;
import utils.ExcelReading;

import javax.xml.ws.WebEndpoint;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AddEmployeeTest extends CommonMethods {

    @Test(groups = "smoke")
    public void addEmployee(){
        //login function
        LoginPage loginPage = new LoginPage();
        loginPage.login(ConfigReader.getPropertyValue("username"), ConfigReader.getPropertyValue("password"));

        DashBoardPage dash = new DashBoardPage();
        clickMethod(dash.pimOption);
        clickMethod(dash.addEmployeeButton);

        //add employee page
        AddEmployeePage addEmployeePage = new AddEmployeePage();
        sendText(addEmployeePage.firstname, "wertyuklkn");
        sendText(addEmployeePage.middleName, "sedxcfgbnkm");
        sendText(addEmployeePage.lastname, "erfghnkm");
        //clickMethod(addEmployeePage.saveBtn);

    }

    @Test()
    public void addMultipleEmployees(){
        //login
        LoginPage loginPage = new LoginPage();
        loginPage.login(ConfigReader.getPropertyValue("username"), ConfigReader.getPropertyValue("password"));
        // navigate to add employee page
        DashBoardPage dash = new DashBoardPage();
        EmployeeListPage emplist = new EmployeeListPage();
        AddEmployeePage addEmployeePage = new AddEmployeePage();
        SoftAssert softAssert = new SoftAssert();


        List<Map<String, String>> newEmployees = ExcelReading.excelIntoListMap(Constants.TESTDATA_FILEPATH,
                "EmployeeData");
        Iterator<Map<String, String>> it = newEmployees.iterator();
        while (it.hasNext()){
            clickMethod(dash.pimOption);
            clickMethod(dash.addEmployeeButton);
            Map<String, String> mapNewEmployee = it.next();
            sendText(addEmployeePage.firstname, mapNewEmployee.get("FirstName"));
            sendText(addEmployeePage.middleName, mapNewEmployee.get("MiddleName"));
            sendText(addEmployeePage.lastname, mapNewEmployee.get("LastName"));

            // capturing employee id from system
            String employeeIdValue = addEmployeePage.employeeId.getAttribute("value");
            sendText(addEmployeePage.photograph, mapNewEmployee.get("Photograph"));
            //select checkbox

            if(!addEmployeePage.checkbox.isSelected()){
                clickMethod(addEmployeePage.checkbox);
            }

            // provide credentials for user
            sendText(addEmployeePage.createUsername, mapNewEmployee.get("Username"));
            sendText(addEmployeePage.createPassword, mapNewEmployee.get("Password"));
            sendText(addEmployeePage.rePassword, mapNewEmployee.get("Password"));
            clickMethod(addEmployeePage.saveBtn);

            //navigate to employee list page
            clickMethod(dash.pimOption);
            clickMethod(dash.employeeListOption);

            sendText(emplist.idEmployee, employeeIdValue);
            clickMethod(emplist.searchBtn);

            List<WebElement> rowData = driver.findElements(By.xpath("//*[@id = 'resultTable']/tbody/tr"));

            for (int i = 0; i < rowData.size(); i++) {
                System.out.println("I am inside the loop to get values for the newly genereated employee");
                String rowText = rowData.get(i).getText();
                System.out.println(rowText);

                String expectedData = employeeIdValue + " " + mapNewEmployee.get("FistName") + " "+
                        mapNewEmployee.get("MiddleName") + " " + mapNewEmployee.get("LastName");
                softAssert.assertEquals(rowText, expectedData);
            }
        }

        softAssert.assertAll();
    }
}
