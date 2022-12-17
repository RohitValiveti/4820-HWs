; This example program checks if a string is in {0^k1^k^2 : k is pos. int}.
; Input: a string of 0's and 1's, eg '001111'

; Machine starts in state 0.

; State 0: moving right, until we see first 0, replace it C
0 0 C r q1
0 _ _ * halt-accept
0 * * * halt-reject

; State q1: moving right until we see 1, replace it by x
q1 0 0 r q1
q1 x x r q1
q1 a a r q1
q1 b b r q1
q1 1 x l q2
q1 _ _ l q6
q1 * * * halt-reject

; State q2: moving left until we see 0/C, a/b (a=0', b=C')
q2 0 a r q1
q2 C b r q1
q2 x x l q2
q2 a a l q2
q2 b b l q2
q2 _ _ r q3
q2 * * * halt-reject

; State q3: moving right until FIRST a=0', replacing all prior b=C' with C
q3 b C r q3
q3 a b r q4
q3 x x r q5
q3 * * * halt-reject

; State q4: move right replacing all a=0' with 0
q4 a 0 r q4
q4 x x r q1
q4 * * * halt-reject

; State q5: While moving right, all numbers should be x (final right check)
q5 _ _ l q6
q5 1 * * halt-reject
q5 x x r q5
q5 * * * halt-reject

; State q6: Move left while equal to x or b=c' and accept if reach _
q6 x x l q6
q6 b b l q6
q6 _ _ * halt-accept
q6 * * * halt-reject