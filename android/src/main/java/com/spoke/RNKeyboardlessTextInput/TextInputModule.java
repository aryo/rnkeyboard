package com.spoke.RNKeyboardlessTextInput;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.uimanager.NativeViewHierarchyManager;
import com.facebook.react.uimanager.UIBlock;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.UIViewOperationQueue;
import com.facebook.react.views.textinput.ReactEditText;

public class TextInputModule extends ReactContextBaseJavaModule {
    private static final String TAG = "TextInputModule";

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public String getName() {
        return "RNKeyboardlessTextInput";
    }


    public TextInputModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    private void addUIBlock(UIBlock block) {
        final UIManagerModule uiManager = this.getReactApplicationContext()
                .getNativeModule(UIManagerModule.class);
        uiManager.addUIBlock(block);
    }

    private ReactEditText getEditTextByReactTag(final NativeViewHierarchyManager viewHierarchyManager, int reactTag) {
        return (ReactEditText) viewHierarchyManager.resolveView(reactTag);
    }

    private int getSelectionStart(final ReactEditText input) {
        return Math.max(input.getSelectionStart(), 0);
    }

    private int getSelectionEnd(final ReactEditText input) {
        return Math.max(input.getSelectionEnd(), 0);
    }

    /**
     * Inserts text at the current selection.
     *
     * IMPORTANT: This absolutely needs to be run on the UI thread, or else there will be sync issues,
     * thus the need to be run by the native view hierarchy manager within a UI block!!
     * @param viewHierarchyManager
     * @param reactTag
     * @param text
     */
    private void insertText(final NativeViewHierarchyManager viewHierarchyManager, int reactTag, final String text) {
        final ReactEditText input = getEditTextByReactTag(viewHierarchyManager, reactTag);
        if (input == null) {
            return;
        }

        final int start = getSelectionStart(input);
        final int end = getSelectionEnd(input);
        input.getText()
                .replace(Math.min(start, end), Math.max(start, end), text, 0, text.length());
    }

    /**
     * Removes text at the current selection.
     *
     * IMPORTANT: This absolutely needs to be run on the UI thread, or else there will be sync issues,
     * thus the need to be run by the native view hierarchy manager within a UI block!!
     * @param viewHierarchyManager
     * @param reactTag
     */
    private void removeText(final NativeViewHierarchyManager viewHierarchyManager, int reactTag) {
        final ReactEditText input = getEditTextByReactTag(viewHierarchyManager, reactTag);
        if (input == null) {
            return;
        }

        final int start = getSelectionStart(input);
        final int end = getSelectionEnd(input);
        final boolean isEmptySelectionRange = start == end;

        final int deletionStart = isEmptySelectionRange
                ? start - 1
                : start;

        input.getText()
                .delete(deletionStart, end);
    }

    @ReactMethod
    public void insertText(final int reactTag, final String text) {
        try {
            addUIBlock((viewHierarchyManager) -> {
                insertText(viewHierarchyManager, reactTag, text);
            });
        } catch (Exception error) {
            Log.e(TAG, "insertText error", error);
        }
    }

    @ReactMethod
    public void removeText(final int reactTag) {
        try {
            addUIBlock((viewHierarchyManager) -> {
                removeText(viewHierarchyManager, reactTag);
            });
        } catch (Exception error) {
            Log.e(TAG, "removeText error", error);
        }
    }
}
