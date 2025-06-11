cl_pr_div(N, C, R) :-
  C * C =< N,
  (N mod C =:= 0, prime(C) -> R = C;
  C1 is C + 1,
  cl_pr_div(N, C1, R)).

prime(N) :-
  N > 1,
  \+ cl_pr_div(N, 2, _).

composite(N) :-
  N > 1,
  cl_pr_div(N, 2, _).

prime_divisors(N, Divisors) :-
	number(N), !,
	gen(N, Divisors).

list_prime([]).
list_prime([H | T]) :-
	prime(H),
	list_prime(T).

sort_up([]).
sort_up([F]).
sort_up([F, S | T]) :-
	F =< S,
	sort_up([S | T]).

%produce(1, []).
%produce(N, [H | T]) :-
%	produce(N1, T),
%	N is H * N1.

gen(1, []) :- !.
gen(N, [H]) :- prime(N), H is N, !.
gen(N, [H | T]) :-
	cl_pr_div(N, 2, R),
	H is R,
	N1 is N / R,
	gen(N1, T), !.

max(A, B, Answ) :-
	A > B, Answ is A;
	A =< B, Answ is B.

max_am([], _, CurAm, MaxAm, Answ) :-
	max(CurAm, MaxAm, Answ).
	
max_am([H | T], Last, CurAm, MaxAm, Answ) :-
	(Last =:= H, !, NewCurAm is CurAm + 1, max_am(T, H, NewCurAm, MaxAm, Answ));
	(Last =\= H, !, max(CurAm, MaxAm, NewMaxAm), max_am(T, H, 1, NewMaxAm, Answ)).

max_am([H | T], Answ) :-
	max_am(T, H, 1, 1, Answ).

power(1, M) :- M is 0, !.
power(N, M) :-
	prime_divisors(N, Div),
	max_am(Div, M).






