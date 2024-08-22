(batch "templates.clp")
(batch "functions.clp")
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Fuzzy variables
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defglobal ?*riskFvar* = (new nrc.fuzzy.FuzzyVariable "risk" 0.0 10.0 "0 to 10"))
(defglobal ?*betAmountFvar* = (new nrc.fuzzy.FuzzyVariable "fund" 0.0 10000.0 "usd"))
(defglobal ?*winningChanceFvar* = (new nrc.fuzzy.FuzzyVariable "winningChance" 0.0 1.0 "0 to 1"))
(defglobal ?*expectedFvar* = (new nrc.fuzzy.FuzzyVariable "expected" 0.0 2.0 "0 to 2"))
(defglobal ?*teamRateFvar* = (new nrc.fuzzy.FuzzyVariable "teamRate" 0.0 10.0 "0 to 10"))
(defglobal ?*rlf* = (new nrc.fuzzy.RightLinearFunction))
(defglobal ?*llf* = (new nrc.fuzzy.LeftLinearFunction))

(load-package nrc.fuzzy.jess.FuzzyFunctions)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Linguistic Terms
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(?*riskFvar* addTerm "low"  (new nrc.fuzzy.RFuzzySet 0.0 4.0 ?*rlf*))
(?*riskFvar* addTerm "medium" (new nrc.fuzzy.TrapezoidFuzzySet 3.0 3.8 5.5 6.0))
(?*riskFvar* addTerm "high"   (new nrc.fuzzy.LFuzzySet 5.5 6.0 ?*llf*))
;
(?*betAmountFvar* addTerm "low"  (new nrc.fuzzy.RFuzzySet 0.0 200.0 ?*rlf*))
(?*betAmountFvar* addTerm "medium" (new nrc.fuzzy.TrapezoidFuzzySet 100.0 200.0 500.0 1000.0))
(?*betAmountFvar* addTerm "high"   (new nrc.fuzzy.LFuzzySet 500.0 1000.0 ?*llf*))

(?*expectedFvar* addTerm "low"  (new nrc.fuzzy.RFuzzySet 0.0 0.4 ?*rlf*))
(?*expectedFvar* addTerm "medium" (new nrc.fuzzy.TrapezoidFuzzySet 0.35 0.4 0.6 0.7))
(?*expectedFvar* addTerm "high"   (new nrc.fuzzy.LFuzzySet 0.6 0.7 ?*llf*))
;
(?*teamRateFvar* addTerm "bad"  (new nrc.fuzzy.RFuzzySet 0.0 4.0 ?*rlf*))
(?*teamRateFvar* addTerm "medium" (new nrc.fuzzy.TrapezoidFuzzySet 3.0 3.8 5.5 6.0))
(?*teamRateFvar* addTerm "good"   (new nrc.fuzzy.LFuzzySet 5.5 6.0 ?*llf*))
;
(?*winningChanceFvar* addTerm "low"  (new nrc.fuzzy.RFuzzySet 0.0 0.4 ?*rlf*))
(?*winningChanceFvar* addTerm "medium" (new nrc.fuzzy.TrapezoidFuzzySet 0.35 0.4 0.6 0.7))
(?*winningChanceFvar* addTerm "high"   (new nrc.fuzzy.LFuzzySet 0.6 0.7 ?*llf*))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Bet Amount Rules 
;;
;; The following rules fuzzify the amount of a bet.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrule bet-amount-high-fuzzy
   (Preference (bet-amount ?ba))
 =>
   (assert (PreferenceFuzzy 
            	(betAmountFv (new nrc.fuzzy.FuzzyValue ?*betAmountFvar*
                              (new nrc.fuzzy.PIFuzzySet ?ba 10))
                )))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Team Rate Rules
;;
;; The following rules fuzzify the rate of a team from a given crisp value.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defrule team-rate-fuzzy
   (Team (sport ?s) (name ?n) (rate ?r))
 =>
   (assert (TeamAnalysis (sport ?s) (name ?n) 
           				 (rateFv (new nrc.fuzzy.FuzzyValue ?*teamRateFvar*
                              (new nrc.fuzzy.PIFuzzySet ?r 1)))))
)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Winning Chace Rules
;;
;; The following rules are about how the rate of two teams affect the winning chance. 
;; For example, if teamW is good and teamL is bad then winningChance is very high
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrule good-bad-very-high
   (TeamAnalysis (name ?teamWName) (rateFv ?rw&:(fuzzy-match ?rw "good")))
   (TeamAnalysis (name ?teamLName) (rateFv ?rl&:(fuzzy-match ?rl "bad")))
   (Bet (bid ?bid) (teamW ?teamWName) (teamL ?teamLName)) 
 =>
   (assert (BetAnalysis (bid ?bid) (match-score (fuzzy-rule-match-score))
           				(winningChanceFv (new nrc.fuzzy.FuzzyValue ?*winningChanceFvar* "very high"))))
)

(defrule good-medium-high
   (TeamAnalysis (name ?teamWName) (rateFv ?rw&:(fuzzy-match ?rw "good")))
   (TeamAnalysis (name ?teamLName) (rateFv ?rl&:(fuzzy-match ?rl "medium")))
   (Bet (bid ?bid) (teamW ?teamWName) (teamL ?teamLName)) 
 =>
   (assert (BetAnalysis (bid ?bid) (match-score (fuzzy-rule-match-score))
           				(winningChanceFv (new nrc.fuzzy.FuzzyValue ?*winningChanceFvar* "high"))))
)

(defrule good-good-medium
   (TeamAnalysis (name ?teamWName) (rateFv ?rw&:(fuzzy-match ?rw "good")))
   (TeamAnalysis (name ?teamLName) (rateFv ?rl&:(fuzzy-match ?rl "good")))
   (Bet (bid ?bid) (teamW ?teamWName) (teamL ?teamLName)) 
 =>
   (assert (BetAnalysis (bid ?bid) (match-score (fuzzy-rule-match-score))
           				(winningChanceFv (new nrc.fuzzy.FuzzyValue ?*winningChanceFvar* "medium"))))
)

(defrule bad-good-very-low
   (TeamAnalysis (name ?teamWName) (rateFv ?rw&:(fuzzy-match ?rw "bad")))
   (TeamAnalysis (name ?teamLName) (rateFv ?rl&:(fuzzy-match ?rl "good")))
   (Bet (bid ?bid) (teamW ?teamWName) (teamL ?teamLName)) 
 =>
   (assert (BetAnalysis (bid ?bid) (match-score (fuzzy-rule-match-score))
           				(winningChanceFv (new nrc.fuzzy.FuzzyValue ?*winningChanceFvar* "very low"))))
)
(defrule bad-medium-low
   (TeamAnalysis (name ?teamWName) (rateFv ?rw&:(fuzzy-match ?rw "bad")))
   (TeamAnalysis (name ?teamLName) (rateFv ?rl&:(fuzzy-match ?rl "medium")))
   (Bet (bid ?bid) (teamW ?teamWName) (teamL ?teamLName)) 
 =>
   (assert (BetAnalysis (bid ?bid) (match-score (fuzzy-rule-match-score))
           				(winningChanceFv (new nrc.fuzzy.FuzzyValue ?*winningChanceFvar* "low"))))
)

(defrule bad-bad-medium
   (TeamAnalysis (name ?teamWName) (rateFv ?rw&:(fuzzy-match ?rw "bad")))
   (TeamAnalysis (name ?teamLName) (rateFv ?rl&:(fuzzy-match ?rl "bad")))
   (Bet (bid ?bid) (teamW ?teamWName) (teamL ?teamLName)) 
 =>
   (assert (BetAnalysis (bid ?bid) (match-score (fuzzy-rule-match-score))
           				(winningChanceFv (new nrc.fuzzy.FuzzyValue ?*winningChanceFvar* "medium"))))
)

(defrule medium-good-low
   (TeamAnalysis (name ?teamWName) (rateFv ?rw&:(fuzzy-match ?rw "medium")))
   (TeamAnalysis (name ?teamLName) (rateFv ?rl&:(fuzzy-match ?rl "good")))
   (Bet (bid ?bid) (teamW ?teamWName) (teamL ?teamLName)) 
 =>
   (assert (BetAnalysis (bid ?bid) (match-score (fuzzy-rule-match-score))
           				(winningChanceFv (new nrc.fuzzy.FuzzyValue ?*winningChanceFvar* "low"))))
)

(defrule medium-medium-medium
   (TeamAnalysis (name ?teamWName) (rateFv ?rw&:(fuzzy-match ?rw "medium")))
   (TeamAnalysis (name ?teamLName) (rateFv ?rl&:(fuzzy-match ?rl "medium")))
   (Bet (bid ?bid) (teamW ?teamWName) (teamL ?teamLName)) 
 =>
   (assert (BetAnalysis (bid ?bid) (match-score (fuzzy-rule-match-score))
           				(winningChanceFv (new nrc.fuzzy.FuzzyValue ?*winningChanceFvar* "medium"))))
)

(defrule medium-bad-high
   (TeamAnalysis (name ?teamWName) (rateFv ?rw&:(fuzzy-match ?rw "medium")))
   (TeamAnalysis (name ?teamLName) (rateFv ?rl&:(fuzzy-match ?rl "bad")))
   (Bet (bid ?bid) (teamW ?teamWName) (teamL ?teamLName)) 
 =>
   (assert (BetAnalysis (bid ?bid) (match-score (fuzzy-rule-match-score))
           				(winningChanceFv (new nrc.fuzzy.FuzzyValue ?*winningChanceFvar* "high"))))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Retract Bad Matches
;;
;; This rule will retract unwanted result, only the match with the highest 
;; match score survives.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrule retract-bad-bet-analysis-matches
   ?b1<-(BetAnalysis (bid ?bid) (match-score ?ms1))
   ?b2<-(BetAnalysis (bid ?bid) (match-score ?ms2&:(> ?ms1 ?ms2)))
 =>
   (retract ?b2)
)

(defrule retract-bad-risk-matches
   ?r1<-(RiskFuzzy (bid ?bid) (match-score ?ms1))
   ?r2<-(RiskFuzzy (bid ?bid) (match-score ?ms2&:(> ?ms1 ?ms2)))
 =>
   (retract ?r2)
)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Calculate crisp winning rate values
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrule calculate-winning-rate
   	?b<-(BetAnalysis (winningChanceFv ?wcFv))
  	=> 
  	(modify ?b (winning-rate (?wcFv momentDefuzzify)))
)





;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Expected Value Fuzzy Rules
;;
;; The following rules calculate Calculate expected value and turn it into fuzzy values with PIFuzzySet.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defrule calculate-expected-value
    (Bet (bid ?bid) (odds ?b-odds))
    (BetAnalysis (bid ?bid) (winning-rate ?wr))
  	=> 
    (if (not (eq ?wr nil)) then
        (assert (ExpectedFuzzy (bid ?bid)
            		 	 (expectedFv (new nrc.fuzzy.FuzzyValue ?*expectedFvar*
                             		 (new nrc.fuzzy.PIFuzzySet (calculate-ev ?b-odds ?wr) 0.1)))))
    )
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Evaluate risk
;;
;; The following rules evaluate the risk.
;; For example if you put small money on a bet and the expected value is high then the risk is low.
;; But if you put a lot of money on that bet, the risk will become high.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrule risk-low-1
   (PreferenceFuzzy (betAmountFv ?baFv&:(fuzzy-match ?baFv "low")))
   (ExpectedFuzzy (bid ?bid) (expectedFv ?eFv&:(fuzzy-match ?eFv "high"))) 
 =>
   (assert (RiskFuzzy (bid ?bid) (match-score (fuzzy-rule-match-score))
            		 (riskFv (new nrc.fuzzy.FuzzyValue ?*riskFvar* "low"))))
)

(defrule risk-mid-1
   (PreferenceFuzzy (betAmountFv ?baFv&:(fuzzy-match ?baFv "low")))
   (ExpectedFuzzy (bid ?bid) (expectedFv ?eFv&:(fuzzy-match ?eFv "medium"))) 
 =>
   (assert (RiskFuzzy (bid ?bid) (match-score (fuzzy-rule-match-score))
            		 (riskFv (new nrc.fuzzy.FuzzyValue ?*riskFvar* "medium"))))
)

(defrule risk-mid-2
   (PreferenceFuzzy (betAmountFv ?baFv&:(fuzzy-match ?baFv "low")))
   (ExpectedFuzzy (bid ?bid) (expectedFv ?eFv&:(fuzzy-match ?eFv "low"))) 
 =>
   (assert (RiskFuzzy (bid ?bid) (match-score (fuzzy-rule-match-score))
            		 (riskFv (new nrc.fuzzy.FuzzyValue ?*riskFvar* "medium"))))
)

(defrule risk-mid-3
   (PreferenceFuzzy (betAmountFv ?baFv&:(fuzzy-match ?baFv "medium")))
   (ExpectedFuzzy (bid ?bid) (expectedFv ?eFv&:(fuzzy-match ?eFv "high"))) 
 =>
   (assert (RiskFuzzy (bid ?bid) (match-score (fuzzy-rule-match-score))
            		 (riskFv (new nrc.fuzzy.FuzzyValue ?*riskFvar* "medium"))))
)

(defrule risk-mid-4
   (PreferenceFuzzy (betAmountFv ?baFv&:(fuzzy-match ?baFv "medium")))
   (ExpectedFuzzy (bid ?bid) (expectedFv ?eFv&:(fuzzy-match ?eFv "medium"))) 
 =>
   (assert (RiskFuzzy (bid ?bid) (match-score (fuzzy-rule-match-score))
            		 (riskFv (new nrc.fuzzy.FuzzyValue ?*riskFvar* "medium"))))
)
(defrule risk-high-1
   (PreferenceFuzzy (betAmountFv ?baFv&:(fuzzy-match ?baFv "medium")))
   (ExpectedFuzzy (bid ?bid) (expectedFv ?eFv&:(fuzzy-match ?eFv "low"))) 
 =>
   (assert (RiskFuzzy (bid ?bid) (match-score (fuzzy-rule-match-score))
            		 (riskFv (new nrc.fuzzy.FuzzyValue ?*riskFvar* "high"))))
)

(defrule risk-high-2
   (PreferenceFuzzy (betAmountFv ?baFv&:(fuzzy-match ?baFv "high")))
   (ExpectedFuzzy (bid ?bid) (expectedFv ?eFv&:(fuzzy-match ?eFv "low"))) 
 =>
   (assert (RiskFuzzy (bid ?bid) (match-score (fuzzy-rule-match-score))
            		 (riskFv (new nrc.fuzzy.FuzzyValue ?*riskFvar* "very high"))))
)

(defrule risk-high-3
   (PreferenceFuzzy (betAmountFv ?baFv&:(fuzzy-match ?baFv "high")))
   (ExpectedFuzzy (bid ?bid) (expectedFv ?eFv&:(fuzzy-match ?eFv "medium"))) 
 =>
   (assert (RiskFuzzy (bid ?bid) (match-score (fuzzy-rule-match-score))
            		 (riskFv (new nrc.fuzzy.FuzzyValue ?*riskFvar* "very high"))))
)

(defrule risk-high-4
   (PreferenceFuzzy (betAmountFv ?baFv&:(fuzzy-match ?baFv "high")))
   (ExpectedFuzzy (bid ?bid) (expectedFv ?eFv&:(fuzzy-match ?eFv "high"))) 
 =>
   (assert (RiskFuzzy (bid ?bid) (match-score (fuzzy-rule-match-score))
            		 (riskFv (new nrc.fuzzy.FuzzyValue ?*riskFvar* "high"))))
)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Print Results
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrule print-risk
;;set salience -100 to make sure the result fires last.
    (declare (salience -100))
    (RiskFuzzy (bid ?bid) (riskFv ?riskFv) (match-score ?ms1))
  	=> 
  	(printout t "Bet id= " ?bid ", risk = " (?riskFv momentDefuzzify) "; match score:" ?ms1 crlf)
;; Uncomment to show the fuzzy value plot.
;    (printout t (?riskFv plotFuzzyValue "*") crlf)
)



