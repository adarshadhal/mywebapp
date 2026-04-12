# internet getway Creation
resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.cicd-vpc.id

  tags = {
    Name = "my-ig"
  }
}

