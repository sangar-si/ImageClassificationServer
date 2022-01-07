exception IllegalArgument {
1: string message;
}

service ImageQueryService {
	list<i32> imageQuery (1: string filenames) throws (1: IllegalArgument e);
	void registerBEinFE (1: string beNode, 2: i32 bePort) throws (1: IllegalArgument e);
}
 
