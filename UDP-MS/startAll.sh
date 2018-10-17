echo "1"
exec ./scripts/start.sh MonitorMS.config &
echo "2"
exec ./scripts/start.sh LoggerMS.config &
echo "3"
exec ./scripts/start.sh ClockMS.config &
echo "4"
exec ./scripts/start.sh CheckClockMS.config &

echo "Done"


