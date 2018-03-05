package box;

import box.threads.StateConsumer;

public class Consumer {

	public static void main(String[] args) {
		new Thread(new StateConsumer()).start();

	}

}
