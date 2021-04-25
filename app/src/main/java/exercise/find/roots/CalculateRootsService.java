package exercise.find.roots;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class CalculateRootsService extends IntentService {

    @Override
    public void sendBroadcast(Intent intent) {
        super.sendBroadcast(intent);
    }


    public CalculateRootsService() {
        super("CalculateRootsService");
    }

    /**
     * checks if 20 seconds passed
     * @param timeStartMs time when service began
     * @return true if 20 have passed since service began
     */
    private boolean timePassed(long timeStartMs)
    {
        long currTimeMs = System.currentTimeMillis();
        return CalculateRootsService.msToSec(currTimeMs - timeStartMs) >= 20;
    }

    /**
     * checks if given number is prime
     * @param num number to check
     * @param timeStartMs time when service began
     * @return 1 if number is prime, 0 if not, and -1 if check took more than 20 seconds
     */
    private int isPrimeNumber(long num, long timeStartMs)
    {
        if (num <= 1 || num % 2 == 0 || num % 3 == 0){
            return 0;
        }
        if (num == 2 || num == 3){
            return 0;
        }

        for (int i = 5; i*i <= num; i += 6)
        {
            if (num % i == 0 || num % (i+2) == 0)
            {
                return 0;
            }

            if (timePassed(timeStartMs)) // TODO: should check every iteration or every X iterations?
            {
                return -1;
            }
        }
        return 1;
    }

    /**
     * finds roots for a non-prime number, and puts them in roots
     * @param num number to find roots for
     * @param roots long array [2] where roots will be. if calculation takes more than 20 seconds - root[0] = -1
     * @param timeStartMs time in ms when service began
     */
    private void calculateRoots(long num, long[] roots, long timeStartMs)
    {
        int i = 2;
        while (true)
        {
            if (num % i == 0) // i is root of num
            {
                roots[0] = i;
                roots[1] = num / i;
                return;
            }
            if (timePassed(timeStartMs)) // TODO: should check every iteration or every X iterations?
            {
                roots[0] = -1;
                roots[1] = System.currentTimeMillis();
                return;
            }
            i++;
        }
    }

    /**
     * converts ms to sec
     */
    private static long msToSec(long timeInMs)
    {
        return timeInMs*1000; //TODO check conversion
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;
        long timeStartMs = System.currentTimeMillis();
        long numberToCalculateRootsFor = intent.getLongExtra("number_for_service", 0);
        if (numberToCalculateRootsFor <= 0) {
            Log.e("CalculateRootsService", "can't calculate roots for non-positive input" + numberToCalculateRootsFor);
            return;
        }
    /*
    TODO:
     calculate the roots.
     check the time (using `System.currentTimeMillis()`) and stop calculations if can't find an answer after 20 seconds
     upon success (found a root, or found that the input number is prime):
      send broadcast with action "found_roots" and with extras:
       - "original_number"(long)
       - "root1"(long)
       - "root2"(long)
     upon failure (giving up after 20 seconds without an answer):
      send broadcast with action "stopped_calculations" and with extras:
       - "original_number"(long)
       - "time_until_give_up_seconds"(long) the time we tried calculating

      examples:
       for input "33", roots are (3, 11)
       for input "30", roots can be (3, 10) or (2, 15) or other options
       for input "17", roots are (17, 1)
       for input "829851628752296034247307144300617649465159", after 20 seconds give up
     */
        int isPrime = isPrimeNumber(numberToCalculateRootsFor, timeStartMs);
        Intent outIntent = new Intent();
        switch (isPrime)
        {
            case 1: // prime
                outIntent.setAction("found_roots");
                outIntent.putExtra("original_number", numberToCalculateRootsFor);
                outIntent.putExtra("root1", numberToCalculateRootsFor);
                outIntent.putExtra("root2", 1);
                break;
            case -1: // took more than 20 seconds to check if prime
                long timeWeStoppedMs = System.currentTimeMillis();
                outIntent.setAction("stopped_calculations");
                outIntent.putExtra("original_number", numberToCalculateRootsFor);
                outIntent.putExtra("time_until_give_up_seconds", CalculateRootsService.msToSec(timeWeStoppedMs - timeStartMs));
                break;
            case 0: // non-prime
                long[] roots = new long[2];
                calculateRoots(numberToCalculateRootsFor, roots, timeStartMs);
                if (roots[0] == -1) // took more than 20 seconds to find roots
                {
                    outIntent.setAction("stopped_calculations");
                    outIntent.putExtra("original_number", numberToCalculateRootsFor);
                    outIntent.putExtra("time_until_give_up_seconds", CalculateRootsService.msToSec(roots[1] - timeStartMs));
                }
                else // found roots
                {
                    outIntent.setAction("found_roots");
                    outIntent.putExtra("original_number", numberToCalculateRootsFor);
                    outIntent.putExtra("root1", roots[0]);
                    outIntent.putExtra("root2", roots[1]);
                }
                break;
        }
        sendBroadcast(outIntent);
    }
}