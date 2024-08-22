;; ------------------------------------------------------------
;; Deffunctions
;; ------------------------------------------------------------

(deffunction is-in-timeslot (?timeslot-start ?timeslot-end ?povided-start ?provided-end)
  "Check if the provided time interval is in the time slot."
  (and (< ?provided-end ?timeslot-end) (> ?povided-start ?timeslot-start))
  )

(deffunction calculate-ev (?o ?wr)
  "Calculate expected value."
  (* ?wr (- ?o 1))
  )