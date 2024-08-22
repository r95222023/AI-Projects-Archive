(batch "facts.clp")
;;;**********************
;;;In this examples, I will show some basic idea of how to assert facts
;;;First we need one Preference as follows, which includes all required slots.
;;;Notice that we let time interval = now+- 100000 seconds since the preference will be considered as invalid 
;;;and be removed if the current time is later than time-end.    
;;;**********************
(assert (Preference (pid pref-1)
        			(sport NFL)
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
;;;**********************
;;;In this examples, I will show some basic idea of how to assert facts
;;;First we need one Preference as follows, which includes all required slots.
;;;Notice that we let time interval = now+- 100000 seconds since the preference will be considered as invalid 
;;;and be removed if the current time is later than time-end.    
;;;**********************
(assert (Bet (bid bet1)
    		(sport NFL) 
    		(bet-type Straight-Wagers)
    		(bet-type-arg-1 Win)
     		(bet-type-arg-2 Empty)
    		(odds 1.85)
    		(winning-rate 0.6)
    		(timeslot-start (- (time) 200000))
 			(timeslot-end (+ (time) 200000))
    		(currency USD)
    		(fee 0.01)
    		(min-amount 5)
    		(max-amount 10000)
    		(provider Bet365)))

(assert (Bet (bid bet2)
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

(assert (Bet (bid bet3)
    		(sport NFL) 
    		(bet-type Straight-Wagers)
    		(bet-type-arg-1 Lose)
     		(bet-type-arg-2 Empty)
    		(odds 2.5)
    		(winning-rate 0.7)
    		(timeslot-start (- (time) 200000))
 			(timeslot-end (+ (time) 200000))
    		(currency USD)
    		(fee 0.01)
    		(min-amount 5)
    		(max-amount 10000)
    		(provider Bet365)))

(run)
