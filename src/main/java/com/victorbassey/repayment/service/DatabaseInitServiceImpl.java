package com.victorbassey.repayment.service;

import com.victorbassey.repayment.model.Customer;
import com.victorbassey.repayment.model.CustomerSummary;
import com.victorbassey.repayment.model.RepaymentUpload;
import com.victorbassey.repayment.model.Season;
import com.victorbassey.repayment.repository.CustomerRepository;
import com.victorbassey.repayment.repository.CustomerSummaryRepository;
import com.victorbassey.repayment.repository.RepaymentUploadRepository;
import com.victorbassey.repayment.repository.SeasonRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class DatabaseInitServiceImpl implements DatabaseInitService {

    private SeasonRepository seasonRepository;
    private CustomerRepository customerRepository;
    private CustomerSummaryRepository customerSummaryRepository;
    private RepaymentUploadRepository repaymentUploadRepository;

    public DatabaseInitServiceImpl(SeasonRepository seasonRepository, CustomerRepository customerRepository,
                                   CustomerSummaryRepository customerSummaryRepository,
                                   RepaymentUploadRepository repaymentUploadRepository) {
        this.seasonRepository = seasonRepository;
        this.customerRepository = customerRepository;
        this.customerSummaryRepository = customerSummaryRepository;
        this.repaymentUploadRepository = repaymentUploadRepository;
    }

    @Override
    @PostConstruct
    public void loadDataIntoDB() {
        File file = new File("src/main/resources/data.json");
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(file.getAbsolutePath())) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            JSONArray seasons = (JSONArray) jsonObject.get("Seasons");
            JSONArray customers = (JSONArray) jsonObject.get("Customers");
            JSONArray customerSummaries = (JSONArray) jsonObject.get("CustomerSummaries");
            JSONArray repaymentUploads = (JSONArray) jsonObject.get("RepaymentUploads");

            customers.forEach(customerObj -> customerRepository
                    .save(getCustomerFromObject((JSONObject) customerObj)));

            seasons.forEach(seasonObj -> seasonRepository
                    .save(getSeasonFromObject((JSONObject) seasonObj)));

            customerSummaries.forEach(customerSummaryObj -> customerSummaryRepository
                    .save(getCustomerSummaryFromObject((JSONObject) customerSummaryObj)));

            repaymentUploads.forEach(repaymentUploadObj -> repaymentUploadRepository
                    .save(getRepaymentUploadFromObject((JSONObject) repaymentUploadObj)));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private Season getSeasonFromObject(JSONObject seasonObject) {
        Season newSeason = new Season();
        newSeason.setSeasonId((Long) seasonObject.get("SeasonID"));
        newSeason.setSeasonName((String) seasonObject.get("SeasonName"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        String startDateString = (String) seasonObject.get("StartDate");
        newSeason.setStartDate(LocalDate.parse(startDateString, formatter));

        try {
            String EndDateString = (String) seasonObject.get("EndDate");
            newSeason.setEndDate(LocalDate.parse(EndDateString, formatter));
        } catch (ClassCastException e) {
            newSeason.setEndDate(null);
        }
        return newSeason;
    }

    private Customer getCustomerFromObject(JSONObject customerObject) {
        Customer newCustomer = new Customer();
        newCustomer.setCustomerId((Long) customerObject.get("CustomerID"));
        newCustomer.setCustomerName((String) customerObject.get("CustomerName"));
        return newCustomer;
    }

    private CustomerSummary getCustomerSummaryFromObject(JSONObject customerSummaryObject) {
        CustomerSummary newCustomerSummary = new CustomerSummary();
        newCustomerSummary.setCustomerId((Long) customerSummaryObject.get("CustomerID"));
        newCustomerSummary.setSeasonId((Long) customerSummaryObject.get("SeasonID"));
        newCustomerSummary.setTotalCredit((Long) customerSummaryObject.get("Credit"));
        newCustomerSummary.setTotalRepaid((Long) customerSummaryObject.get("TotalRepaid"));
        return newCustomerSummary;
    }

    private RepaymentUpload getRepaymentUploadFromObject(JSONObject repaymentUploadObject) {
        RepaymentUpload newRepaymentUpload = new RepaymentUpload();
        newRepaymentUpload.setCustomerId((Long) repaymentUploadObject.get("CustomerID"));
        Long seasonId = (Long) repaymentUploadObject.get("SeasonID");
        if (seasonId != 0) {
            newRepaymentUpload.setSeasonId(seasonId);
        }
        newRepaymentUpload.setAmount((Long) repaymentUploadObject.get("Amount"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        String dateString = (String) repaymentUploadObject.get("Date");
        newRepaymentUpload.setDate(LocalDate.parse(dateString, formatter));
        return newRepaymentUpload;
    }
}
