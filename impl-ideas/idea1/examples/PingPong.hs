module PingPong where

import Control.Concurrent
import Control.Concurrent.Actor
import Control.Monad.IO.Class

main :: IO ()
main = do
    ponger <- new pong
    pinger <- new (ping ponger)
    pinger ! ""
    threadDelay 10000000

pong :: Behavior (String, ActorId String)
pong = Behavior $ \(str, pinger) -> do
    liftIO $ putStrLn str
    pinger ! str ++ "pong"

ping :: ActorId (String, ActorId String) -> Behavior String
ping ponger = Behavior $ \str -> do
    liftIO $ putStrLn str
    liftIO $ threadDelay 1000000
    me <- self
    ponger ! (str ++ "ping", me)
