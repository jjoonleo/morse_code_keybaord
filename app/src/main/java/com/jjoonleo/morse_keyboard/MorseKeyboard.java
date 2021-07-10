package com.jjoonleo.morse_keyboard;

import android.app.Service;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import java.security.Key;

import androidx.annotation.NonNull;

public class MorseKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    public class Node{ //class for binary tree's node
        char value;
        Node leftChild;
        Node rightChild;

        public Node(char value){
            this.value = value;
            this.leftChild = null;
            this.rightChild = null;
        }

    }

    Node Tree = new Node('\0');
    Node current = Tree;
    long  onPressTime;
    private KeyboardView kv;
    private Keyboard keyboard;
    private  boolean isSpace = false;       //isSpace?write space:it's time for new character
    private  boolean delete = false;        //delete?still writing the character:it's time for new character
    private  boolean isCaps = false;        //isCaps?uppercase:lowercase



    @Override
    public View onCreateInputView() {
        initTree();     //init binary tree
        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard,null);
        keyboard = new Keyboard(this,R.xml.morse);      //make keyboard and set layout
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        kv.setPreviewEnabled(false);        //disable annoying key preview
        return kv;
    }

    @Override
    public void onPress(int i) {
        onPressTime = System.currentTimeMillis();           //save the pressing time
        Log.d("onPress", "onPress"+onPressTime);
    }

    @Override
    public void onRelease(int i) {
        Log.d("isSpace",""+isSpace);
        InputConnection ic = getCurrentInputConnection();
        switch (i)
        {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1,0);
                current = Tree;
                isSpace = false;
                delete = false;
                break;
            case Keyboard.KEYCODE_SHIFT:
                isCaps = !isCaps;
                keyboard.setShifted(isCaps);
                kv.invalidateAllKeys();
                isSpace = false;
                delete = false;
                break;

            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_ENTER));
                current = Tree;
                isSpace = false;
                delete = false;
                break;

            case 32: //space bar
                current = Tree;
                if(isSpace == true){
                    ic.commitText(String.valueOf((char)32),1);
                    isSpace = false;
                }else {
                    isSpace = true;
                }
                delete = false;
                break;

            case -200:
                break;

            default:
                i = System.currentTimeMillis() - onPressTime > 200 ? 1 : 0;     //check if the user pushed the button longer than 200millis
                if(i == 1){//long
                    if(current.rightChild != null){         //if this node has right child
                        current = current.rightChild;       //go to right child
                    }else{
                        current = Tree.rightChild;          //else go to root node
                    }

                }else if(i == 0){//short
                    if(current.leftChild != null){          //if this node has left child
                        current = current.leftChild;        //go to left child
                    }else{
                        current = Tree.leftChild;           //else go to root node
                    }
                }
                char code = (char)current.value;            //get the value of current node
                if(Character.isLetter(code) && isCaps)      //caps lock
                    code = Character.toUpperCase(code);
                if(delete == true){
                    ic.deleteSurroundingText(1,0);
                }
                ic.commitText(String.valueOf(code),1);      //write text
                Log.d("",""+current.value);
                isSpace = false;
                delete = true;


        }
        Log.d("onRelease", "onRelease"+System.currentTimeMillis());
    }

    @Override
    public void onKey(int i, int[] ints) {

    }



    private void initTree(){//올바른 자리에 알파벳 저장하는데 급해서 노가다 했습니다...
        Tree.leftChild = new Node('e');
        Tree.leftChild.leftChild = new Node('i');
        Tree.leftChild.leftChild.leftChild = new Node('s');
        Tree.leftChild.leftChild.leftChild.leftChild = new Node('h');
        Tree.leftChild.leftChild.leftChild.rightChild = new Node('v');
        Tree.leftChild.leftChild.rightChild = new Node('u');
        Tree.leftChild.leftChild.rightChild.leftChild = new Node('f');
        Tree.leftChild.rightChild = new Node('a');
        Tree.leftChild.rightChild.leftChild = new Node('r');
        Tree.leftChild.rightChild.leftChild.leftChild = new Node('l');
        Tree.leftChild.rightChild.rightChild = new Node('w');
        Tree.leftChild.rightChild.rightChild.leftChild = new Node('p');
        Tree.leftChild.rightChild.rightChild.rightChild = new Node('j');

        Tree.rightChild = new Node('t');
        Tree.rightChild.leftChild = new Node('n');
        Tree.rightChild.leftChild.leftChild = new Node('d');
        Tree.rightChild.leftChild.leftChild.leftChild = new Node('b');
        Tree.rightChild.leftChild.leftChild.rightChild = new Node('x');
        Tree.rightChild.leftChild.rightChild = new Node('k');
        Tree.rightChild.leftChild.rightChild.leftChild = new Node('c');
        Tree.rightChild.leftChild.rightChild.rightChild = new Node('y');
        Tree.rightChild.rightChild = new Node('m');
        Tree.rightChild.rightChild.leftChild = new Node('g');
        Tree.rightChild.rightChild.leftChild.leftChild = new Node('z');
        Tree.rightChild.rightChild.leftChild.rightChild = new Node('q');
        Tree.rightChild.rightChild.rightChild = new Node('o');
    }

    @Override
    public void onText(CharSequence charSequence) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}