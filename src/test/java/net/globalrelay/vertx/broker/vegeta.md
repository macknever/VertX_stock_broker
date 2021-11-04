# Vegeta Load Testing

'echo "GET http://localhost:8888/assets" | vegeta attack -workers=16 -max-workers=32 -duration=30s | tee results.bin |
vegeta report
'

echo "GET http://localhost:8888/quote/AMZN" | vegeta attack -duration=30s -workers=10 -max-workers=16