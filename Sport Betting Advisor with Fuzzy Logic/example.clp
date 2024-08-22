(batch "facts.clp")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; In this examples, I will show some basic idea of how to assert facts
;; Firstly, we need a Preference as follows, which includes all required slots.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(assert (Preference (pid pref-1)
        			(sport NBA)
    			    (bet-type Straight-Wagers)
    			   	(bet-type-arg-1 Win)
    			   	(bet-type-arg-2 Empty)
    			   	(currency USD)
;; Bet amount is crucial to the risk evaluation. Higer bet amount usually
;; means higher risk
    			   	(bet-amount 100)
    			   	(provider Bet365)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Then we need assert some imaginary bets, which require some basic imformations as follows.
;; TeamW means the winning team you put your bet on. TeamL is the opponent. You can find teams 
;; in fact.clp where you can modify the rate of teams.
;; When betting, odds are often the ratio of winnings to the stake and you also get your wager returned. 
;; So wagering 1 at 1:5 pays out 6 (5+1). If you make 6 wagers of 1, and win once and lose 5 times, you will be paid 6 and finish square. 
;; Wagering 1 at 1:1 (Evens) pays out 2 (1+1) and wagering 2 at 1:2 pays out 3 (1+2). 
;; Here we use Decimal or Continental odds, the ratio of total paid out to stake: Ex 6.0, 2.0, 1.5
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(assert (Bet (bid bet1)
    		(sport NBA) 
    		(bet-type Straight-Wagers)
    		(bet-type-arg-1 Win)
     		(bet-type-arg-2 Empty)
        	(teamW Milwaukee-Bucks)
        	(teamL Chicago-Bulls)
    		(odds 1.85)
    		(currency USD)
    		(provider Bet365)))

(assert (Bet (bid bet2)
    		(sport NBA)
    		(bet-type Straight-Wagers)
    		(bet-type-arg-1 Win)
     		(bet-type-arg-2 Empty)
        	(teamW Chicago-Bulls)
        	(teamL Milwaukee-Bucks)
    		(odds 5)
    		(currency USD)
    		(provider Bet365)))


(assert (Bet (bid bet3)
    		(sport NBA)
    		(bet-type Straight-Wagers)
    		(bet-type-arg-1 Win)
     		(bet-type-arg-2 Empty)
        	(teamW LA-Lakers)
        	(teamL Utah-Jazz)
    		(odds 2.3)
    		(currency USD)
    		(provider Bet365)))

;; After inserting all bets and a preference, we can start running the program
(run)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; The result should look like following
;;
;; Bet id= bet1, risk = 2.943843510948738; match score:0.5241935483870968
;; Bet id= bet2, risk = 4.537635427621121; match score:0.5241935483870968
;; Bet id= bet3, risk = 1.537869440037605; match score:0.5241935483870968
;;
;; The risk is out of 10, we can see all risks are low to medium
;; since we only put small bet on those bets (bet amount=100). 
;; We can also see that bet3 has lower risk since Lakers performed better than 
;; Jazz this season. And the odds is relatively high compares to Bulls-Bucks game. 
;; You can add more team with arbitrary rate into facts.clp to see how this works.

;; Uncomment to show the active facts while running.
;;(facts)