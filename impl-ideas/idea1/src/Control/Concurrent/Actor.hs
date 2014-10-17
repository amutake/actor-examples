{-# LANGUAGE GeneralizedNewtypeDeriving #-}
{-# OPTIONS_GHC -fno-warn-unused-do-bind #-}

module Control.Concurrent.Actor
    ( Behavior (..)
    , Actor
    , ActorId
    , (!)
    , self
    , become
    , new
    ) where

import Control.Applicative
import Control.Concurrent
import Control.Concurrent.STM.TQueue
import Control.Monad.IO.Class
import Control.Monad.Reader
import Control.Monad.State
import Control.Monad.STM

newtype Behavior t = Behavior (t -> Actor t ())

newtype Actor t a = Actor { runActor :: StateT (Behavior t) (ReaderT (ActorId t) IO) a }
  deriving (Functor, Applicative, Monad, MonadReader (ActorId t), MonadState (Behavior t), MonadIO)

type ActorId t = TQueue t

stm :: MonadIO m => STM a -> m a
stm = liftIO . atomically

infixr 0 !
(!) :: MonadIO m => ActorId t -> t -> m ()
actorId ! msg = stm $ writeTQueue actorId msg

self :: Actor t (ActorId t)
self = ask

become :: Behavior t -> Actor t ()
become = put

new :: MonadIO m => Behavior t -> m (ActorId t)
new behavior = liftIO $ do
    queue <- newTQueueIO
    forkIO $ flip runReaderT queue $ flip evalStateT behavior $ runActor loop
    return queue
  where
    loop = do
      (Behavior f) <- get
      queue <- ask
      msg <- stm $ readTQueue queue
      f msg
      loop
