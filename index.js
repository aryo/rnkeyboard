import React, { useEffect, useRef, useImperativeHandle, forwardRef } from "react";

import {
  NativeModules,
  TextInput,
  findNodeHandle,
  Platform
} from 'react-native';

const { RNKeyboardlessTextInput } = NativeModules;
const { setup, insertText, removeText } = RNKeyboardlessTextInput;
const isIOS = Platform.OS === "ios";

export const KeyboardlessTextInput = forwardRef((props, ref) => {
  const inputRef = useRef(null);
  const getReactTag = () => findNodeHandle(inputRef.current);

  // Hiding keyboard is not available out of the box on iOS, so we need to set it up natively to use a dummy view
  isIOS && useEffect(() => {
    setup(getReactTag());
  }, []);

  useImperativeHandle(ref, () => {
    const reactTag = getReactTag();
    return {
      insert: text => insertText(reactTag, text),
      del: () => removeText(reactTag)
    };
  });

  return (
      <TextInput
          {...props}
          keyboardType={'numeric'}
          ref={inputRef}
          showSoftInputOnFocus={false} // Android only
      />
  );
});

KeyboardlessTextInput.propTypes = TextInput.propTypes;
