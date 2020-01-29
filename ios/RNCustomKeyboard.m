
#import "RNCustomKeyboard.h"
#import "RCTBridge+Private.h"
#import "RCTUIManager.h"
#import "RCTBaseTextInputView.h"

@implementation RNCustomKeyboard

@synthesize bridge = _bridge;

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

- (UITextView*)getTextView:(nonnull NSNumber *)reactTag {
    return (UITextView *)(((RCTBaseTextInputView*)[_bridge.uiManager viewForReactTag:reactTag]).backedTextInputView);
}

RCT_EXPORT_MODULE(RNKeyboardlessTextInput)

RCT_EXPORT_METHOD(setup:(nonnull NSNumber *)reactTag)
{
    UITextView *view = [self getTextView:reactTag];
    UIView *dummyView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 1, 1)];

    view.inputView = dummyView;
    [view reloadInputViews];
}

RCT_EXPORT_METHOD(insertText:(nonnull NSNumber *)reactTag withText:(NSString*)text) {
    UITextView *view = [self getTextView:reactTag];
    [view replaceRange:view.selectedTextRange withText:text];
}

RCT_EXPORT_METHOD(removeText:(nonnull NSNumber *)reactTag) {
    UITextView *view = [self getTextView:reactTag];
    UITextRange* selectedRange = view.selectedTextRange;
    const BOOL isEmptySelectionRange = [view comparePosition:selectedRange.start toPosition:selectedRange.end] == 0;
    
    UITextRange* range = isEmptySelectionRange // delete a single char if selection range is empty
        ? [view textRangeFromPosition:[view positionFromPosition:selectedRange.start offset:-1] toPosition:selectedRange.start]
        : selectedRange;
    
    [view replaceRange:range withText:@""];
}

@end

