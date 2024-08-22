(batch "templates.clp")
(batch "functions.clp")
; ------------------------------------------------------------
; Fact Removal Rules
; ------------------------------------------------------------
(defrule remove-pref
	?r <- (Remove (pid ?pid))
    ?p <- (Preference (pid ?pid))
  	=> 
  	(retract ?p)
    (retract ?r)
  	(printout t "Remove Preference from working space: " ?pid crlf))

(defrule remove-bet
	?r <- (Remove (bid ?bid))
    ?b <- (Bet (bid ?bid))
  	=> 
  	(retract ?b)
    (retract ?r)
  	(printout t "Remove Preference from working space: " ?bid crlf))
;; ------------------------------------------------------------
;; Preference Rules
;; The following rules validate Preference by checking its consistaency to Provider Sport and Bet-Type
;; ------------------------------------------------------------

(defrule check-preference-required
	(not ?p <- (Preference (pid ?pid)
            		 (sport ?sport) 
					 (bet-type ?bet-type)
    			 	 (time-start ?time-start)
    			 	 (time-end ?time-end)
    				 (risk-bound-high ?risk-bound-high)
     				 (risk-bound-mid ?risk-bound-mid)
    				 (risk-bound-low ?risk-bound-low)
    				 (portion-high ?portion-high)
     				 (portion-mid ?portion-mid)
    			 	 (currency ?currency)
            		 (country ?country)
            		 (payment-method ?payment-method)
    			 	 (provider ?provider))
        )
	=>
;    (assert (Remove (pid ?pid))) 
    (retract ?p)
	(printout t "check-pref-required fail: " ?pid crlf))

(defrule check-preference-sport
	(and 
		 	?p <- (Preference (pid ?pid) (sport ?name) (bet-type ?pref-type))
			(not (Sport (name ?name)))
         )
  	=> 
;  	(assert (Remove (pid ?pid)))
    (retract ?p)
  	(printout t "check-pref-sport fail: " ?pid crlf))

(defrule check-preference-provider
	(and 
		 	?p <- (Preference (pid ?pid) (bet-type ?pref-type) (provider ?pref-provider))
			(not (Provider (name ?pref-provider)))
         )
  	=> 
;  	(assert (Remove (pid ?pid)))
    (retract ?p)
  	(printout t "check-pref-provider fail: " ?pid crlf))

(defrule check-preference-time
	?p <- (Preference (pid ?pid) (time-end ?p-time-end&:(> (time) ?p-time-end)))
  	=>
	(retract ?p)
  	(printout t "check-pref-time fail: " ?pid crlf))

(defrule check-preference-country
	(and
         ?p <- (Preference (pid ?pid) (country ?country))
  		 (not (Provider (name ?provider) (country-available $?country-1 ?country $?country-2)))
	)
	=> 
	(retract ?p)
  	(printout t "check-pref-country fail: " ?pid crlf))

;; ------------------------------------------------------------
;; Bet Rules
;;
;; The following rules validate bets by checking its consistaency to Provider Sport and Bet-Type
;; ------------------------------------------------------------
(defrule verify-bet-type
  	?b <- (Bet (bid ?bid) (bet-type ?b-type) (bet-type-arg-1 ?arg-1) (bet-type-arg-2 ?arg-2))
  	(not (Bet-Type (name ?b-type)
           	  (bet-type-arg-1-available $?arg1start ?arg1 $?arg1end)
           	  (bet-type-arg-2-available $?arg2start ?arg2 $?arg2end)))
  	=> 
	(retract ?b)
    (printout t "check-bet-type fail: " ?bid crlf))
	
(defrule verify-bet-provider
	?b <- (Bet (bid ?bid) (provider ?b-provider) (fee ?b-fee))
    (not (Provider (name ?b-provider) (available ?p-available)(fee ?b-fee)))
  	=> 
  	(retract ?b)
    (printout t "check-bet-provider fail: " ?bid crlf))

(defrule verify-bet-validity
  	(not ?b <- (Bet (odds ?b-odds&:(> ?b-odds 1))
            	  	(winning-rate ?b-w-rate&:(> ?b-w-rate 0)&:(< ?b-w-rate 1))
             	  	(timeslot-start ?b-t-start)
            	  	(timeslot-end ?b-t-end&:(> ?b-t-end ?b-t-start))
   	))
  	=> 
 	(retract ?b)
    (printout t "check-bet-validity fail: " ?bid crlf))

;; ------------------------------------------------------------
;; Match Rules
;;
;; The following rules matches preferences and bets 
;; ------------------------------------------------------------
(defrule match-provider
  	(Provider (name ?b-provider) (available TRUE))
   	(Preference (provider $?provider1 ?b-provider $?provider2))
    (not ?b <- (Bet (bid ?bid) (provider ?b-provider)))
  	=> 
  	(retract ?b)
    (printout t "match-provider fail: " ?bid crlf))
        
(defrule match-timeslot
    ?b <- (Bet (bid ?bid) (timeslot-start ?b-t-start)
            	  (timeslot-end ?b-t-end))
    (Preference (time-start ?p-t-start)
               (time-end ?p-t-end))
	=> 
    (if (not (is-in-timeslot ?b-t-start ?b-t-end ?p-t-start ?p-t-end)) then 
        (assert (Remove (bid ?bid)))
        (printout t "match-timeslot fail: " ?bid crlf)))

            
(defrule match-sport
	?b <- (Bet (sport ?b-sport))
	(not (Preference (sport $?p-sport1 ?b-sport $?p-sport2)))
  	=> 
	(retract ?b))
                
(defrule match-bet-type
	(not ?b <- (Bet (bet-type ?b-bet-type)
                  	(bet-type-arg-1 ?b-arg1) 
    			  	(bet-type-arg-2 ?b-arg2)))
	(Preference (bet-type $?p-btype1 ?b-bet-type $?p-btype2)
                (bet-type-arg-1 $?p-barg11 ?b-arg1 $?p-barg12)
              	(bet-type-arg-2 $?p-barg21 ?b-arg2 $?p-barg22))
  	=> 
  	(retract ?b))
                    
(defrule match-currency
	(not ?b <- (Bet (currency ?b-currency)))
    (Preference (currency $?p-currency1 ?b-currency $?p-currency2))
  	=> 
  	(retract ?b))
                        
(defrule match-payment-method
  	(not ?b <- (Bet (provider ?b-provider)))
  	(Preference (payment-method ?p-method))
  	(Provider (name ?b-provider) (payment-method-available $?p-method1 ?p-method $?p-method2))
  	=> 
  	(retract ?b))
    
(defrule match-country-method
  	(not ?b <- (Bet (provider ?b-provider)))
  	(Preference (country ?p-country))
   	(Provider (name ?b-provider) (country-available $?p-country1 ?p-country $?p-country2))
  	=> 
  	(retract ?b))

;; ------------------------------------------------------------
;; Analysis Rules
;;
;; The following rules calculate expected value of a bet and 
;; suggested bet amount (portion*bet-amount) then add it to Analysis for each bet.
;; ------------------------------------------------------------
(defrule calculate-expected-value
   	(Bet (bid ?bid) (odds ?b-odds) (winning-rate ?b-w-rate))
  	=> 
  	(assert (Analysis (bid ?bid) (expected-value (calculate-ev ?b-odds ?b-w-rate))))
 )

(defrule calculate-bet-amount-high
    (Preference (portion-high ?ph) (bet-amount ?ba))
    ?a <- (Analysis (bid ?bid) (expected-value ?ev) (risk High))
   	(Bet (bid ?bid) (min-amount ?bmina&:(< ?bmina (* ?ba ?ph))) (max-amount ?bmaxa&:(> ?bmaxa (* ?ba ?ph))))
  	=> 
  	(modify ?a (suggested-bet-amount (* ?ba ?ph)))
 )

(defrule calculate-bet-amount-mid
    (Preference (portion-mid ?pm) (bet-amount ?ba))
    ?a <- (Analysis (bid ?bid) (expected-value ?ev) (risk Mid))
   	(Bet (bid ?bid) (min-amount ?bmina&:(< ?bmina (* ?ba ?pm))) (max-amount ?bmaxa&:(> ?bmaxa (* ?ba ?pm))))
  	=> 
  	(modify ?a (suggested-bet-amount (* ?ba ?pm)))
 )

(defrule calculate-bet-amount-low
    (Preference (portion-low ?pl) (bet-amount ?ba))
    ?a <- (Analysis (bid ?bid) (expected-value ?ev) (risk Low))
   	(Bet (bid ?bid) (min-amount ?bmina&:(< ?bmina (* ?ba ?pl))) (max-amount ?bmaxa&:(> ?bmaxa (* ?ba ?pl))))
  	=> 
  	(modify ?a (suggested-bet-amount (* ?ba ?pl)))
 )

(defrule find-high-risk
    (Bet (bid ?bid))
    (Preference (risk-bound-high ?p-risk-bound-high) (risk-bound-mid ?p-risk-bound-mid))
    ?a <- (Analysis (bid ?bid) (expected-value ?a-ev&:(<= ?a-ev ?p-risk-bound-mid)&:(> ?a-ev ?p-risk-bound-high)))
  	=> 
  	(modify ?a (risk High))
 )
    
(defrule find-mid-risk
    (Bet (bid ?bid))
    (Preference (risk-bound-mid ?p-risk-bound-mid) (risk-bound-low ?p-risk-bound-low))
    ?a <- (Analysis (bid ?bid) (expected-value ?a-ev&:(<= ?a-ev ?p-risk-bound-low)&:(> ?a-ev ?p-risk-bound-mid)))
  	=> 
  	(modify ?a (risk Mid))
 )
    
(defrule find-low-risk
    (Bet (bid ?bid))
    (Preference (risk-bound-low ?p-risk-bound-low))
    ?a <- (Analysis (bid ?bid) (expected-value ?a-ev&:(> ?a-ev ?p-risk-bound-low)))
  	=> 
  	(modify ?a (risk Low))
 )

;; ------------------------------------------------------------
;; Print Results
;; ------------------------------------------------------------
(defrule print-high-risk
    (Analysis (bid ?bid) (risk High) (expected-value ?ev) (suggested-bet-amount ?sba))
  	=> 
  	(printout t "High risk bet found: id= " ?bid ", expected value= " ?ev ", suggested bet amount= " ?sba crlf)
 )
    
(defrule print-mid-risk
    (Analysis (bid ?bid) (risk Mid) (expected-value ?ev) (suggested-bet-amount ?sba))
  	=> 
  	(printout t "Mid risk bet found: id= " ?bid ", expected value= " ?ev ", suggested bet amount= " ?sba crlf)
 )
    
(defrule print-low-risk
    (Analysis (bid ?bid) (risk Low) (expected-value ?ev) (suggested-bet-amount ?sba))
  	=> 
  	(printout t "Low risk bet found: id= " ?bid ", expected value= " ?ev ", suggested bet amount= " ?sba crlf)
 )