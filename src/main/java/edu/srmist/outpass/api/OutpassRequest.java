package edu.srmist.outpass.api;

/** JSON body of POST /outpass. Each field becomes a process variable. */
public record OutpassRequest(
        String studentName,
        String regNo,
        String hostelBlock,
        String outDate,       // yyyy-MM-dd
        String returnDate,    // yyyy-MM-dd
        String parentPhone,
        String purpose) {
}
