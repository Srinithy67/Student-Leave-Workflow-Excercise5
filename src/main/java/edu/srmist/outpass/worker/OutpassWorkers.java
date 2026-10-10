package edu.srmist.outpass.worker;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OutpassWorkers {

    private static final Logger log = LoggerFactory.getLogger(OutpassWorkers.class);

    /**
     * Service task "Check request".
     * Reads : regNo, outDate, returnDate
     * Writes: nights, valid, approved (true only for a same-day out-pass)
     */
    @JobWorker(type = "check-outpass")
    public Map<String, Object> checkOutpass(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        LocalDate out = LocalDate.parse((String) vars.get("outDate"));
        LocalDate back = LocalDate.parse((String) vars.get("returnDate"));

        long nights = ChronoUnit.DAYS.between(out, back);
        boolean valid = !back.isBefore(out);          // invalid if return date < out date
        boolean autoApproved = valid && nights == 0;  // day-out is auto-approved

        log.info("Check out-pass for {}: nights={}, valid={}, autoApproved={}",
                vars.get("regNo"), nights, valid, autoApproved);

        return Map.of("nights", nights, "valid", valid, "approved", autoApproved);
    }

    /**
     * Service task "Issue out-pass".
     * Reads : regNo, outDate, studentName, parentPhone, approved, wardenRemarks
     * Writes: passStatus (ISSUED / REJECTED), passNumber (only when ISSUED)
     */
    @JobWorker(type = "issue-outpass")
    public Map<String, Object> issueOutpass(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        boolean approved = Boolean.TRUE.equals(vars.get("approved"));
        String regNo = String.valueOf(vars.get("regNo"));
        String outDate = String.valueOf(vars.get("outDate"));
        String studentName = String.valueOf(vars.get("studentName"));
        String parentPhone = String.valueOf(vars.get("parentPhone"));
        Object remarks = vars.get("wardenRemarks");

        Map<String, Object> result = new HashMap<>();
        if (approved) {
            String passNumber = "OP-" + regNo + "-" + outDate;
            result.put("passStatus", "ISSUED");
            result.put("passNumber", passNumber);
            log.info("SMS to parent {}: out-pass {} ISSUED for {}. Warden remarks: {}",
                    parentPhone, passNumber, studentName, remarks);
        } else {
            result.put("passStatus", "REJECTED");
            log.info("SMS to parent {}: out-pass for {} REJECTED. Warden remarks: {}",
                    parentPhone, studentName, remarks);
        }
        return result;
    }
}
