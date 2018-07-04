package bmutil.ExcelReader.JavaBean;

import com.pharbers.aqll.common.alFileHandler.alExcelOpt.scala.BaseExcel;

/**
 * Created by yym on 7/27/17.
 */
public class RawData extends BaseExcel{
    private String GenericName;
    private String CompanyName;
    private String Year;
    private Long SalesAmount;
    private Long Quantity;
    private String Specification;
    private String Formulation;
    private Integer Quarter;
    private Integer SinglePackage;
    private String ROA;
    private String TherapyMicro;
    private String TherapyWide;
    private String City;

    public String getGenericName() {
        return GenericName;
    }

    public void setGenericName(String genericName) {
        GenericName = genericName;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public Long getSalesAmount() {
        return SalesAmount;
    }

    public void setSalesAmount(Long salesAmount) {
        SalesAmount = salesAmount;
    }

    public Long getQuantity() {
        return Quantity;
    }

    public void setQuantity(Long quantity) {
        Quantity = quantity;
    }

    public String getSpecification() {
        return Specification;
    }

    public void setSpecification(String specification) {
        Specification = specification;
    }

    public String getFormulation() {
        return Formulation;
    }

    public void setFormulation(String formulation) {
        Formulation = formulation;
    }

    public int getQuarter() {
        return Quarter;
    }

    public void setQuarter(Integer quarter) {
        Quarter = quarter;
    }

    public int getSinglePackage() {
        return SinglePackage;
    }

    public void setSinglePackage(Integer singlePackage) {
        SinglePackage = singlePackage;
    }

    public String getROA() {
        return ROA;
    }

    public void setROA(String ROA) {
        this.ROA = ROA;
    }

    public String getTherapyMicro() {
        return TherapyMicro;
    }

    public void setTherapyMicro(String therapyMicro) {
        TherapyMicro = therapyMicro;
    }

    public String getTherapyWide() {
        return TherapyWide;
    }

    public void setTherapyWide(String therapyWide) {
        TherapyWide = therapyWide;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }





}
