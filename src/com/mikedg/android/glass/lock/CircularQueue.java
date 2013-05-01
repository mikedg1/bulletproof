/*
Copyright 2013 Michael DiGiovanni

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.mikedg.android.glass.lock;

import java.util.LinkedList;

public class CircularQueue<E> extends LinkedList<E> {

    private int mLimit;

    public CircularQueue(int limit) {
        this.mLimit = limit;
    }

    @Override
    public boolean add(E object) {
        super.add(object);
        while (size() > mLimit) {
            super.remove();
        }
        return true;
    }
}