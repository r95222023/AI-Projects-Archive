(batch "facts.clp")
;;-----------------
;; Assume we are interesting in betting on NBA and NFL, we can simply put NBA NFL into 'sport' slot which is 
;; defined as multislot. To see how it works,  you can check 'match-sport' rule in rules.clp 
;; Similarly, 'bet-type',  'bet-type-arg-1', 'currency', 'country', 'payment-method', 'provider' are also multislot
;; and can be used in the same way as 'sport' slot.
;;--------------
(assert (Preference (sport NBA NFL)
    			    (bet-type Straight-Wagers)
    			   	(bet-type-arg-1 Win)
    			   	(bet-type-arg-2 Empty)
    			   	(currency USD)
    			   	(country USA)
    			   	(time-start (- (time) 100000))
    			   	(time-end (+ (time) 100000))
    			   	(risk-bound-high 0.01)
    			   	(risk-bound-mid  0.5)
    			   	(risk-bound-low  1.01)
    			   	(portion-high 0.1)
			       	(portion-mid 0.3)
        			(portion-low 0.6)
    			   	(payment-method Paypal)
    			   	(bet-amount 100)
    			   	(provider Bet365)))

(assert (Bet (bid bet1)
    		(sport NBA) 
    		(bet-type Straight-Wagers)
    		(bet-type-arg-1 Win)
     		(bet-type-arg-2 Empty)
    		(odds 1.85)
    		(winning-rate 0.6)
    		(timeslot-start (- (time) 200000))
 			(timeslot-end (+ (time) 200000))
    		(currency USD)
    		(fee 0.01)
    		(min-amount 50)
    		(max-amount 10000)
    		(provider Bet365)))
;;-----------------
;; Notice that the bet is on MLB, so it won't show up in the result since MLB is not in sport slot in preference 
;;------------------
(assert (Bet (bid bet2)
    		(sport MLB) 
    		(bet-type Straight-Wagers)
    		(bet-type-arg-1 Lose)
     		(bet-type-arg-2 Empty)
    		(odds 2.5)
    		(winning-rate 0.1)
    		(timeslot-start (- (time) 200000))
 			(timeslot-end (+ (time) 200000))
    		(currency USD)
    		(fee 0.01)
    		(min-amount 5)
    		(max-amount 10000)
    		(provider Bet365)))

(assert (Bet (bid bet3)
    		(sport NFL) 
    		(bet-type Straight-Wagers)
    		(bet-type-arg-1 Lose)
     		(bet-type-arg-2 Empty)
    		(odds 2.5)
    		(winning-rate 0.8)
    		(timeslot-start (- (time) 200000))
 			(timeslot-end (+ (time) 200000))
    		(currency USD)
    		(fee 0.01)
    		(min-amount 5)
    		(max-amount 10000)
    		(provider Bet365)))
(run)

;;-----------------
;; The odds of sport betting can change very fast since there are some type of betting allow client to 
;; bet on things about to happen. So it will be nice to see if Jess (basically Java) can run dynamically.
;; To simulate querrying new facts from database, we need sleep function in JAVA since Jess dont have it.
;; We can create a new java object in Jess and bind it to ?thread as follows.
;; The sleep method of this object can be used to pause the programe to simulate querrying 
;; new facts from database. We can then call sleep as follows 
;;------------------
(bind ?thread (new java.lang.Thread))
(printout t "Simulating retrieving new bets from database" crlf)
(printout t "New bet coming in 5 seconds" crlf)
(call ?thread sleep 5000)
(assert (Bet (bid bet4)
    		(sport NFL) 
    		(bet-type Straight-Wagers)
    		(bet-type-arg-1 Lose)
     		(bet-type-arg-2 Empty)
    		(odds 2.5)
    		(winning-rate 0.1)
    		(timeslot-start (- (time) 200000))
 			(timeslot-end (+ (time) 200000))
    		(currency USD)
    		(fee 0.01)
    		(min-amount 5)
    		(max-amount 10000)
    		(provider Bet365)))
(run)
(printout t "success!!" crlf)
;(facts)