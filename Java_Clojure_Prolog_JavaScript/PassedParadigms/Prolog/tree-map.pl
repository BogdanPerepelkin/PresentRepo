length([], 0).
length([H | T], R) :- 
	length(T, RT),
	R is RT + 1.
	
map_build(ListMap, TreeMap) :-
	length(ListMap, Len),
	build(Len, ListMap, _Rest, TreeMap).


build(0, Sour, Sour, nil) :- !.
build(Len, Sour, Rest, T) :-
	LenL is Len // 2 + Len mod 2 - 1,
	LenR is Len // 2,
	build(LenL, Sour, [(Rk, Rv) | Rsour], Left),
	build(LenR, Rsour, Rest, Right),
	T = node(Rk, Rv, Left, Right).


map_get(nil, Key, Value) :- false.
map_get(node(K, V, Left, Right), Key, Value) :-
	Key < K, !, map_get(Left, Key, Value).
map_get(node(K, V, Left, Right), Key, Value) :-
	Key > K, !, map_get(Right, Key, Value).
map_get(node(K, V, Left, Right), Key, Value) :-
	Key =:= K, Value = V.

map_containsValue(nil, _) :- false.
map_containsValue(node(K, Value, Left, Right), Value) :- !.
map_containsValue(node(K, V, Left, Right), Value) :-
	map_containsValue(Left, Value), !.
map_containsValue(node(K, Value, Left, Right), Value) :-
	map_containsValue(Right, Value), !.

map_values(nil, []) :- !.
map_values(node(K, V, Left, Right), Values) :-
  map_values(Left, LeftValues),
  map_values(Right, RightValues),
  append([V], RightValues, Temp),
  append(LeftValues, Temp, Values).
	
%map_get(TreeMap, Key, Value) :-
%	TreeMap = node(K, V, Left, Right),
%	(
%		(Key < K, !, map_get(Left, Key, Value));
%		(Key > K, !, map_get(Right, Key, Value));
%		(Key =:= K, Value = V)
%	).

%map_containsValue(Map, Value) :- 
%	Map = node(K, V, Left, Right),
%	(
%		(V == Value, true);
%		map_containsValue(Left, Value), !;
%		map_containsValue(Right, Value)
%	).

	







