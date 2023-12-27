package Rx;

import java.util.HashMap;

public interface RxTypeParser<T> {
    public T Convert(String rawValue);
}