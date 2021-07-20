private void messagesSync(GroupChannel groupChannel) {
    MessageFilter messageFilter = new MessageFilter(
            BaseChannel.MessageTypeFilter.ALL, null, null
    );
    messageCollection = new MessageCollection(groupChannel, messageFilter, Long.MAX_VALUE);
    MessageCollectionHandler messageCollectionHandler = new MessageCollectionHandler() {
        @Override
        public void onMessageEvent(MessageCollection collection, List<BaseMessage> messages, MessageEventAction action) {
            // Deprecated
        }
        @Override
        public void onSucceededMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action) {
            switch (action) {
                case INSERT:
                    Log.i(TAG, "SUCCEEDED MESSAGES INSERTED");
                    processMessages(messages, "I", "ok");
                    break;
                case REMOVE:
                    Log.i(TAG, "REMOVE SUCCEEDED MESSAGE");
                    processMessages(messages, "R", "ok");
                    break;
                case UPDATE:
                    break;
                case CLEAR:
                    break;
            }
        }
        @Override
        public void onPendingMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action) {
            switch (action) {
                case INSERT:
                    Log.i(TAG, "PENDING MESSAGES INSERTED");
                    processMessages(messages, "I", "pending");
                    break;
                case REMOVE:
                    Log.i(TAG, "REMOVE PENDING MESSAGE");
                    processMessages(messages, "R", "pending");
                    break;
            }
        }
        @Override
        public void onFailedMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action, final FailedMessageEventActionReason reason) {
            switch (action) {
                case INSERT:
                    Log.i(TAG, "FAILED MESSAGES INSERTED");
                    processMessages(messages, "I", "failed");
                    break;
                case REMOVE:
                    Log.i(TAG, "FAILED MESSAGES REMOVED");
                    processMessages(messages, "R", "failed");
                    break;
                case UPDATE:
                    break;
            }
        }
        @Override
        public void onNewMessage(MessageCollection collection, BaseMessage message) {
        }
    };
    messageCollection.setCollectionHandler(messageCollectionHandler);
    messageCollection.fetchSucceededMessages(MessageCollection.Direction.PREVIOUS, new FetchCompletionHandler() {
        @Override
        public void onCompleted(boolean hasMore, SendBirdException e) {
            messageCollection.fetchSucceededMessages(MessageCollection.Direction.NEXT, new FetchCompletionHandler() {
                @Override
                public void onCompleted(boolean hasMore, SendBirdException e) {
                    if (e != null) {
                        Log.e(TAG, "ERROR FETCHING SUCCEEDED MESSAGES");
                    }
                    messageCollection.fetchFailedMessages(new CompletionHandler() {
                        @Override
                        public void onCompleted(SendBirdException e) {
                            if (e != null) {
                                Log.e(TAG, "ERROR FETCHING FAILED MESSAGES");
                            }
                        }
                    });
                }
            });
        }
    });
}